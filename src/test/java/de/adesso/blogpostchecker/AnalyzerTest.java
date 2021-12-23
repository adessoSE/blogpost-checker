package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnalyzerTest extends BaseTest {

    @BeforeAll
    public void setup() {
        try {
            File dir = new File("src/test/resources/devblog");
            if (dir.exists()) {
                deleteFiles(dir);
            }

            LocalRepoCreator.setLocalGit(Git.cloneRepository()
                    .setDirectory(dir)
                    .setURI("https://github.com/jo2/devblog")
                    .call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDown() {
        deleteFiles(new File("src/test/resources/"));
    }

    private void deleteFiles(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFiles(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @Test
    public void getPostMetadataFromGitShouldPass() {
        PostMetadata metadata = new PostMetadata();
        metadata.setAuthor("dariobraun");
        metadata.setCategories("Test");
        metadata.setDate("2020-04-01 13:00");
        metadata.setLayout("post, post-xml");
        metadata.setTags("Tag1, Tag2, Tag3");
        metadata.setTitle("sdfs dasd 23 dsadas & fdsf");
        Assertions.assertThat(fileAnalyzer.getMetadata().equals(metadata));
    }

    @Test
    public void getAuthorFromGitShouldPass() {
        Author author = new Author();
        author.setAuthorNickname("dariobraun");
        author.setAvatarUrl("/assets/images/avatars/dariobraun.jpg");
        author.setBio("Dario Braun ist Werkstudent bei adesso in Dortmund und im Open-Source-Bereich tätig.");
        author.setEmail("dario.braun@adesso.de");
        author.setFirstName("Dario");
        author.setGithub("https://github.com/dariobraun");
        author.setGithubUsername("dariobraun");
        author.setLastName("Braun");
        Assertions.assertThat(fileAnalyzer.getAuthors().equals(List.of(author)));
    }
}
