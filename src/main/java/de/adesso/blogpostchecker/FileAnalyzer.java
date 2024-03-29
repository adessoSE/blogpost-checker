package de.adesso.blogpostchecker;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
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
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileAnalyzer.class);

    private final ConfigService configService;

    private boolean analyzed;
    private PostMetadata metadata;
    private List<Author> authors;
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

    public List<Author> getAuthors() {
        if (!analyzed) {
            analyzeBranch();
        }
        return authors;
    }

    private void analyzeBranch() {
        try {
            localGitInstance = LocalRepoCreator.getLocalGit();

            LOGGER.info("Analyzing diff between head commit '{}' and base commit '{}'",
                    ObjectId.fromString(configService.getHEAD_COMMIT()),
                    ObjectId.fromString(configService.getBASE_COMMIT()));

            int limit = 0;
            for (RevCommit commit : localGitInstance.log().call()) {
                LOGGER.info("{} ({}): {}", commit.getName(), new Date(commit.getCommitTime() * 1000L), commit.getShortMessage());
                if (limit++ > 10) {
                    break;
                }
            }

            RevCommit baseCommit = localGitInstance.getRepository()
                    .parseCommit(ObjectId.fromString(configService.getBASE_COMMIT()));
            RevCommit currentHead = localGitInstance.getRepository()
                    .parseCommit(ObjectId.fromString(configService.getHEAD_COMMIT()));

            LOGGER.info("Analysing commit {}: {} by {}", currentHead.getName(), currentHead.getShortMessage(),
                    currentHead.getAuthorIdent().getEmailAddress());

            DiffEntry markdownPost = extractNewPostFromCommitDifference(currentHead, baseCommit);
            extractMetadataFromFiles(currentHead, markdownPost);
            localGitInstance.close();
        } catch (IOException e) {
            LOGGER.error("Could not analyze branch", e);
            ExitBlogpostChecker.exit(LOGGER, "Error on getting file content: " + e.getMessage(), 25);
        } catch (GitAPIException e) {
            LOGGER.error("GitApiException: ", e);
            ExitBlogpostChecker.exit(LOGGER, "Error on getting file content: " + e.getMessage(), -1);
        }
    }

    private void extractMetadataFromFiles(RevCommit latestCommit, DiffEntry blogPost) {
        if (blogPost != null) {
            LOGGER.info("Analysing post {}", blogPost.getNewPath());
            String blogPostPath = blogPost.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? blogPost.getOldPath() : blogPost.getNewPath();
            String blogPostContent = new String(BlobUtils.getRawContent(localGitInstance.getRepository(), latestCommit.toObjectId(), blogPostPath));
            metadata = extractMetadataFromStringUsingRegex(blogPostContent.split("---")[1]);

            authors = getAuthors(latestCommit, metadata.getAuthor());
            if (authors == null) {
                ExitBlogpostChecker.exit(LOGGER, "Error during reading of authors.yml.", 21);
            }

            analyzed = true;
        } else {
            ExitBlogpostChecker.exitInfo(LOGGER, "Found no blog post to check.", 0);
        }
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

    protected PostMetadata extractMetadataFromStringUsingRegex(String metadataString) {
        return PostMetadata.builder()
                .layout(extractAttributeValueFromMetadata(metadataString, "\\nlayout:\\s*\\[post, post-xml].*\\n", "\\[(.*?)]", 1))
                .title(extractAttributeValueFromMetadata(metadataString, "\\ntitle:\\s*(\"|').*(\"|').*\\n", "(\"|')(.*?)(\"|')", 2))
                .date(extractAttributeValueFromMetadata(metadataString, "\\ndate:\\s*\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}.*\\n", "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}", 0))
                .author(extractAttributeValueFromMetadata(metadataString, "\\nauthor_ids:\\s*\\[.*].*\\n", "\\[(.*)]", 1))
                .categories(extractAttributeValueFromMetadata(metadataString, "\\ncategories:\\s*\\[.*].*\\n", "\\[(.*)]", 1))
                .tags(extractAttributeValueFromMetadata(metadataString, "\\ntags:\\s*\\[.*].*\\n", "\\[(.*?)]", 1))
                .build();
    }

    private String extractAttributeValueFromMetadata(String metadataString, String pattern1, String pattern2, int groupIndex) {
        Pattern pattern = Pattern.compile(pattern1);
        Matcher matcher = pattern.matcher(metadataString);
        if (matcher.find()) {
            String extractedLine = matcher.group();
            Pattern stringPattern = Pattern.compile(pattern2);
            Matcher stringMatcher = stringPattern.matcher(extractedLine);
            if (stringMatcher.find() && !"".equals(stringMatcher.group(groupIndex))) {
                return stringMatcher.group(groupIndex);
            }
        }
        return null;
    }

    private List<Author> getAuthors(RevCommit commit, String authorName) {
        Git localGit = LocalRepoCreator.getLocalGit();
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
        return Collections.emptyList();
    }

    private List<Author> getAuthorFromYml(String authorYml, String authorIdsString) {
        Yaml yaml = new Yaml();
        Map<String, LinkedHashMap<String, String>> obj = yaml.load(authorYml);

        return Arrays.stream(authorIdsString.split(",\\s")).map(authorName -> {
            if (obj.containsKey(authorName)) {
                Map<String, String> authorMap = obj.get(authorName);

                return Author.builder()
                        .authorNickname(authorName)
                        .firstName(authorMap.get("first_name"))
                        .lastName(authorMap.get("last_name"))
                        .bio(authorMap.get("bio"))
                        .avatarUrl(authorMap.get("avatar_url"))
                        .build();
            } else {
                ExitBlogpostChecker.exit(LOGGER, MessageFormat.format("The specified author with name \"{0}\" is not listed in authors.yml.",
                        authorIdsString), 26);
            }
            return null;
        }).collect(Collectors.toList());
    }

    private boolean commitsFound(RevCommit commit1, RevCommit commit2) {
        return commit1 != null && commit2 != null;
    }
}
