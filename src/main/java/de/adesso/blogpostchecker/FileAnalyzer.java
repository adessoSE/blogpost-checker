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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FileAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileAnalyzer.class);

    private ConfigService configService;

    private boolean analyzed;
    private PostMetaData metaData;
    private String authors;
    private Git localGitInstance;

    @Autowired
    public FileAnalyzer(ConfigService configService) {
        this.configService = configService;
    }

    public PostMetaData getMetaData() {
        if (!analyzed) {
            analyzeBranch();
        }
        return metaData;
    }

    public String getAuthors() {
        if (!analyzed) {
            analyzeBranch();
        }
        return authors;
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
                extractMetaDataFromFiles(currentHead, markdownPost);
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

    private void extractMetaDataFromFiles(RevCommit latestCommit, DiffEntry blogPost) {
        authors = getAuthors(latestCommit);

        if (authors == null) {
            ExitBlogpostChecker.exit(LOGGER, "Error during reading of authors.yml.", 21);
        }

        if (blogPost != null) {
            String blogPostPath = blogPost.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? blogPost.getOldPath() : blogPost.getNewPath();
            String blogPostContent = new String(BlobUtils.getRawContent(localGitInstance.getRepository(), latestCommit.toObjectId(), blogPostPath));
            metaData = extractMetaDataFromString(blogPostContent.split("---")[1]);
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

    private PostMetaData extractMetaDataFromString(String metaDataString) {
        PostMetaData metaData = new PostMetaData();

        metaData.setLayout(extractAttributeValueFromMetaData(metaDataString, "\\nlayout:\\s*\\[post, post-xml].*\\n", "\\[(.*?)]", 1));
        metaData.setTitle(extractAttributeValueFromMetaData(metaDataString, "\\ntitle:\\s*\".*\".*\\n", "\"(.*?)\"", 1));
        metaData.setDate(extractAttributeValueFromMetaData(metaDataString, "\\ndate:\\s*\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}.*\\n", "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}", 0));
        metaData.setAuthor(extractAttributeValueFromMetaData(metaDataString, "\\nauthor:\\s*\\w+.*\\n", "(\\w+:\\s*)(\\w+)", 2));
        metaData.setCategories(extractAttributeValueFromMetaData(metaDataString, "\\ncategories:\\s*\\[.*].*\\n", "\\[(.*)]", 1));
        metaData.setTags(extractAttributeValueFromMetaData(metaDataString, "\\ntags:\\s*\\[.*].*\\n", "\\[(.*?)]", 1));

        return metaData;
    }

    private String extractAttributeValueFromMetaData(String metaDataString, String pattern1, String pattern2, int groupIndex) {
        Pattern pattern = Pattern.compile(pattern1);
        Matcher matcher = pattern.matcher(metaDataString);
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

    private String getAuthors(RevCommit commit) {
        Repository localRepo = localGitInstance.getRepository();

        try (TreeWalk treeWalk = TreeWalk.forPath(localGitInstance.getRepository(), "_data/authors.yml", commit.getTree());
             ObjectReader objectReader = localRepo.newObjectReader()) {

            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader objectLoader = objectReader.open(blobId);
            byte[] bytes = objectLoader.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            ExitBlogpostChecker.exit(LOGGER, "Error on getting authors.yml content from git.", 26);
        }
        return null;
    }

    private boolean commitsFound(RevCommit commit1, RevCommit commit2) {
        return commit1 != null && commit2 != null;
    }
}
