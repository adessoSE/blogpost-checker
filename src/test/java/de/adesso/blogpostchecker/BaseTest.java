package de.adesso.blogpostchecker;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test"})
public class BaseTest {

    static {
        System.setProperty("HEAD_COMMIT", "61d141398ac10982f0904da8e1d5629c5b859765");
        System.setProperty("BASE_COMMIT", "b6983b887ff737cfeb00fe1dd311968183477d2d");
        System.setProperty("LOCAL_REPO_PATH", "src/test/resources/devblog");
    }

    @Autowired
    protected FileAnalyzer fileAnalyzer;
    @Autowired
    protected GitRepoOpener repoOpener;
    @Autowired
    protected ConfigService configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
