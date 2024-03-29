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
        System.setProperty("HEAD_COMMIT", "db645ab068d8498cd6b31d12803160a192add326");
        System.setProperty("BASE_COMMIT", "04033f5e859553737955b4581843cd588b3d9bc5");
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
