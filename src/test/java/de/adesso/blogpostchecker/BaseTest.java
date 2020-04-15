package de.adesso.blogpostchecker;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BaseTest {

    static {
        System.setProperty("REPOSITORY_REMOTE_URL", "https://github.com/jo2/devblog");
        System.setProperty("REPOSITORY_BRANCH_NAME", "second-post");
    }

    @Autowired
    protected FileAnalyzer fileAnalyzer;
    @Autowired
    protected GitRepoCloner repoCloner;
    @Autowired
    protected ConfigService configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
