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
    private PostHeader header;
    private String authors;

    @Autowired
    public FileAnalyzer(ConfigService configService) {
        this.configService = configService;
    }

    public PostHeader getHeader() {
        if (!analyzed) {
            analyzeBranch();
        }
        return header;
    }

    public String getAuthors() {
        if (!analyzed) {
            analyzeBranch();
        }
        return authors;
    }

    private void analyzeBranch() {
        try {
            Git localGit = LocalRepoCreater.getLocalGit();
            Repository localRepo = localGit.getRepository();

            Ref localBranch = localGit.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                    .stream().filter(ref -> getBranchName(ref).equals(configService.getREPOSITORY_BRANCH_NAME()))
                    .findFirst().orElse(null);

            if (localBranch != null) {
                List<RevCommit> commits = StreamSupport
                        .stream(localGit.log().add(localRepo.resolve(localBranch.getName())).call().spliterator(), false)
                        .collect(Collectors.toList());

                RevCommit firstCommit = commits.get(0);
                RevCommit secondCommit = commits.stream().filter(commit -> commit.getParentCount() > 1).findFirst().orElse(null);
                DiffEntry markdownPost = getPostFromCommitCompare(firstCommit, secondCommit);
                authors = getAuthors(firstCommit);

                if (authors != null && markdownPost != null) {
                    String markdownPath = markdownPost.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? markdownPost.getOldPath() : markdownPost.getNewPath();
                    String content = new String(BlobUtils.getRawContent(localRepo, firstCommit.toObjectId(), markdownPath));
                    header = getHeaderFromString(content.split("---")[1]);
                    analyzed = true;
                } else {
                    if (authors == null) {
                        LOGGER.error("Error during reading of authors.yml");
                    }
                    if (markdownPost == null) {
                        LOGGER.error("Error during reading of markdown file");
                    }
                    LOGGER.error("Exiting BlogpostChecker.");
                    System.exit(21);
                }
            } else {
                LOGGER.error("Error on getting branch from git.");
                LOGGER.error("Exiting BlogpostChecker.");
                System.exit(22);
            }
        } catch (GitAPIException e) {
            LOGGER.error("Error on accessing git api.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(23);
        } catch (IOException e) {
            LOGGER.error("Error on getting file content.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(24);
        }
    }

    private DiffEntry getPostFromCommitCompare(RevCommit firstCommit, RevCommit secondCommit) throws IOException {
        if (firstCommit != null && secondCommit != null) {
            Git localGit = LocalRepoCreater.getLocalGit();
            ObjectReader reader = localGit.getRepository().newObjectReader();

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = firstCommit.getTree();
            newTreeIter.reset(reader, newTree);

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = secondCommit.getTree();
            oldTreeIter.reset(reader, oldTree);

            DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
            df.setRepository(localGit.getRepository());
            List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);
            return entries.stream()
                    .filter(entry -> (
                            entry.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? entry.getOldPath() : entry.getNewPath()
                    ).endsWith(".md"))
                    .findFirst().orElse(null);
        }
        return null;
    }

    private PostHeader getHeaderFromString(String headerString) {
        PostHeader header = new PostHeader();

        header.setLayout(getContent(headerString, "\\nlayout:\\s*\\[post, post-xml].*\\n", "\\[(.*?)]", 1));
        header.setTitle(getContent(headerString, "\\ntitle:\\s*\".*\".*\\n", "\"(.*?)\"", 1));
        header.setDate(getContent(headerString, "\\ndate:\\s*\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}.*\\n", "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}", 0));
        header.setAuthor(getContent(headerString, "\\nauthor:\\s*\\w+.*\\n", "(\\w+:\\s*)(\\w+)", 2));
        header.setCategories(getContent(headerString, "\\ncategories:\\s*\\[.*].*\\n", "\\[(.*)]", 1));
        header.setTags(getContent(headerString, "\\ntags:\\s*\\[.*].*\\n", "\\[(.*?)]", 1));

        return header;
    }

    private String getContent(String headerString, String pattern1, String pattern2, int groupIndex) {
        Pattern pattern = Pattern.compile(pattern1);
        Matcher matcher = pattern.matcher(headerString);
        if (matcher.find()) {
            String titleLine = matcher.group();
            Pattern stringPattern = Pattern.compile(pattern2);
            Matcher stringMatcher = stringPattern.matcher(titleLine);
            return stringMatcher.find() ? stringMatcher.group(groupIndex) : null;
        }
        return null;
    }

    private String getBranchName(Ref ref) {
        String[] branchName = ref.getName().split("/");
        return branchName[branchName.length - 1];
    }

    private String getAuthors(RevCommit commit) {
        Git localGit = LocalRepoCreater.getLocalGit();
        Repository localRepo = localGit.getRepository();

        try (TreeWalk treeWalk = TreeWalk.forPath(localGit.getRepository(), "_data/authors.yml", commit.getTree())) {
            ObjectId blobId = treeWalk.getObjectId(0);
            try (ObjectReader objectReader = localRepo.newObjectReader()) {
                ObjectLoader objectLoader = objectReader.open(blobId);
                byte[] bytes = objectLoader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            LOGGER.error("Error on getting authors.yml content from git.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(25);
        }
        return null;
    }
}
