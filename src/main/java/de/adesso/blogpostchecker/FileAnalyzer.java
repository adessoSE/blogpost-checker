package de.adesso.blogpostchecker;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FileAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileAnalyzer.class);

    private ConfigService configService;

    private boolean analyzed;
    private PostMetadata metadata;
    private Author author;
    private Git localGitInstance;

    @Autowired
    public FileAnalyzer(ConfigService configService) {
        this.configService = configService;
    }

    public PostMetadata getMetadata() {
        if (!analyzed) {
            analyzeBranch();
        }
        return metadata;
    }

    public Author getAuthors() {
        if (!analyzed) {
            analyzeBranch();
        }
        return author;
    }

    private void analyzeBranch() {
        try {
            localGitInstance = LocalRepoCreater.getLocalGit();
            Ref localBranch = getBranchByName();

            if (localBranch != null) {
                List<RevCommit> commits = getCommitsInBranch(localBranch);

                RevCommit currentHead = commits.get(0);
                // a commit that was definitely there before the branch of this pull request was created.
                // this commit is used to have a base to compare the currentHead against.
                RevCommit baseCommit = commits.stream().filter(commit -> commit.getParentCount() > 1).findFirst().orElse(null);
                DiffEntry markdownPost = extractNewPostFromCommitDifference(currentHead, baseCommit);
                extractMetadataFromFiles(currentHead, markdownPost);
            } else {
                ExitBlogpostChecker.exit(LOGGER, "Error on getting branch from git.", 23);
            }
        } catch (GitAPIException e) {
            ExitBlogpostChecker.exit(LOGGER, "Error on accessing git api.", 24);
        } catch (IOException e) {
            ExitBlogpostChecker.exit(LOGGER, "Error on getting file content.", 25);
        }
    }

    private List<RevCommit> getCommitsInBranch(Ref branch) throws IOException, GitAPIException {
        return StreamSupport
                .stream(localGitInstance.log().add(localGitInstance.getRepository().resolve(branch.getName())).call().spliterator(), false)
                .collect(Collectors.toList());
    }

    private void extractMetadataFromFiles(RevCommit latestCommit, DiffEntry blogPost) {
        if (blogPost != null) {
            String blogPostPath = blogPost.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? blogPost.getOldPath() : blogPost.getNewPath();
            String blogPostContent = new String(BlobUtils.getRawContent(localGitInstance.getRepository(), latestCommit.toObjectId(), blogPostPath));
            metadata = extractMetadataFromString(blogPostContent.split("---")[1]);

            author = getAuthors(latestCommit, metadata.getAuthor());
            if (author == null) {
                ExitBlogpostChecker.exit(LOGGER, "Error during reading of authors.yml.", 21);
            }

            analyzed = true;
        } else {
            LOGGER.info("No added blog posts files found.");
            LOGGER.info("Stopping BlogpostChecker.");
            System.exit(0);
        }
    }

    private Ref getBranchByName() throws GitAPIException {
        return localGitInstance.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                .stream().filter(ref -> extractBranchName(ref).equals(configService.getREPOSITORY_BRANCH_NAME()))
                .findFirst().orElse(null);
    }

    private DiffEntry extractNewPostFromCommitDifference(RevCommit firstCommit, RevCommit secondCommit) throws IOException {
        if (commitsFound(firstCommit, secondCommit)) {
            ObjectReader reader = localGitInstance.getRepository().newObjectReader();

            return getDiffsFromCommits(getTreeParser(secondCommit, reader), getTreeParser(firstCommit, reader)).stream()
                    .filter(entry -> entry.getChangeType().equals(DiffEntry.ChangeType.ADD) && entry.getNewPath().endsWith(".md"))
                    .findFirst().orElse(null);
        }
        return null;
    }

    private CanonicalTreeParser getTreeParser(RevCommit commit, ObjectReader reader) throws IOException {
        CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
        ObjectId tree = commit.getTree();
        newTreeParser.reset(reader, tree);
        return newTreeParser;
    }


    private List<DiffEntry> getDiffsFromCommits(CanonicalTreeParser oldTreeParser, CanonicalTreeParser newTreeParser) throws IOException {
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
        df.setRepository(localGitInstance.getRepository());
        return df.scan(oldTreeParser, newTreeParser);
    }

    private PostMetadata extractMetadataFromString(String metadataString) {
        PostMetadata metadata = new PostMetadata();

        metadata.setLayout(extractAttributeValueFromMetadata(metadataString, "\\nlayout:\\s*\\[post, post-xml].*\\n", "\\[(.*?)]", 1));
        metadata.setTitle(extractAttributeValueFromMetadata(metadataString, "\\ntitle:\\s*\".*\".*\\n", "\"(.*?)\"", 1));
        metadata.setDate(extractAttributeValueFromMetadata(metadataString, "\\ndate:\\s*\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}.*\\n", "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}", 0));
        metadata.setAuthor(extractAttributeValueFromMetadata(metadataString, "\\nauthor:\\s*\\w+.*\\n", "(\\w+:\\s*)(\\w+)", 2));
        metadata.setCategories(extractAttributeValueFromMetadata(metadataString, "\\ncategories:\\s*\\[.*].*\\n", "\\[(.*)]", 1));
        metadata.setTags(extractAttributeValueFromMetadata(metadataString, "\\ntags:\\s*\\[.*].*\\n", "\\[(.*?)]", 1));

        return metadata;
    }

    private String extractAttributeValueFromMetadata(String metadataString, String pattern1, String pattern2, int groupIndex) {
        Pattern pattern = Pattern.compile(pattern1);
        Matcher matcher = pattern.matcher(metadataString);
        if (matcher.find()) {
            String extractedLine = matcher.group();
            Pattern stringPattern = Pattern.compile(pattern2);
            Matcher stringMatcher = stringPattern.matcher(extractedLine);
            return stringMatcher.find() ? stringMatcher.group(groupIndex) : null;
        }
        return null;
    }

    private String extractBranchName(Ref ref) {
        String[] branchName = ref.getName().split("/");
        return branchName[branchName.length - 1];
    }

    private Author getAuthors(RevCommit commit, String authorName) {
        Git localGit = LocalRepoCreater.getLocalGit();
        Repository localRepo = localGit.getRepository();

        try (TreeWalk treeWalk = TreeWalk.forPath(localGitInstance.getRepository(), "_data/authors.yml", commit.getTree());
             ObjectReader objectReader = localRepo.newObjectReader()) {

            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader objectLoader = objectReader.open(blobId);
            byte[] bytes = objectLoader.getBytes();
            return getAuthorFromYml(new String(bytes, StandardCharsets.UTF_8), authorName);
        } catch (IOException e) {
            ExitBlogpostChecker.exit(LOGGER, "Error on getting authors.yml content from git.", 26);
        }
        return null;
    }

    private Author getAuthorFromYml(String authorYml, String authorName) {
        Yaml yaml = new Yaml();
        Map<String, LinkedHashMap<String, String>> obj = yaml.load(authorYml);
        if (obj.containsKey(authorName)) {
            Author author = new Author();
            Map<String, String> authorMap = obj.get(authorName);

            author.setDisplayName(authorName);
            author.setFirstName(authorMap.get("first_name"));
            author.setLastName(authorMap.get("last_name"));
            author.setGithubUsername(authorMap.get("github_username"));
            author.setEmail(authorMap.get("email"));
            author.setBio(authorMap.get("bio"));
            author.setAvatarUrl(authorMap.get("avatar_url"));
            author.setGithub(authorMap.get("github"));

            return author;
        } else {
            LOGGER.error("The author not in authors.yml.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(26);
        }
        return null;
    }

    private boolean commitsFound(RevCommit commit1, RevCommit commit2) {
        return commit1 != null && commit2 != null;
    }
}
