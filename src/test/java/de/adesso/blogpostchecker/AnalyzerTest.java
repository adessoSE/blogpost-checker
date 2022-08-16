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
        metadata.setAuthor("fabianvolkert, jo2");
        metadata.setCategories("Softwareentwicklung");
        metadata.setDate("2021-08-23 13:00");
        metadata.setLayout("post, post-xml");
        metadata.setTags("Kotlin, Funktionale Programmierung");
        metadata.setTitle("Functional Kotlin - Eine EinfÃ¼hrung");
        Assertions.assertThat(fileAnalyzer.getMetadata()).isEqualTo(metadata);
    }

    @Test
    public void getAuthorFromGitShouldPass() {
        Author author1 = Author.builder()
                .authorNickname("fabianvolkert")
                .firstName("Fabian")
                .lastName("Volkert")
                .avatarUrl("/assets/images/avatars/fabianvolkert.png")
                .bio("Fabian Volkert ist seit 2020 Software Developer bei adesso. Die ersten Berührungspunkte mit Kotlin hatte er 2019 in seiner Masterarbeit und programmierte in dieser Sprache anschließend auch in einem Projekt im E-Commerce.")
                .github("https://github.com/VolkertF")
                .build();

        Author author2 = Author.builder()
                .authorNickname("jo2")
                .firstName("Johannes")
                .lastName("Teklote")
                .avatarUrl("/assets/images/avatars/johannesteklote.jpg")
                .bio("Johannes Teklote ist Werkstudent bei adesso in Dortmund und entwickelt Software im Open Source Bereich.")
                .github("https://github.com/jo2")
                .build();

        Assertions.assertThat(fileAnalyzer.getAuthors()).isEqualTo(List.of(author1, author2));
    }
}
