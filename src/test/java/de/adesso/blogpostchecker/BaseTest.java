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
        System.setProperty("HEAD_COMMIT", "7d5d8694587a3ace2649fbc4346618f9ac93df7e");
        System.setProperty("BASE_COMMIT", "ead420a5ae7442b03dbfe1d181020f2d56b21e19");
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
