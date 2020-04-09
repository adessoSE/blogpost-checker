package de.adesso.blogpostchecker;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BaseTest {

    @Autowired
    protected FileAnalyzer fileAnalyzer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
