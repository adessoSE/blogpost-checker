package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorTest extends BaseTest {

    @Test
    public void hasAuthor() {
        String headerString = "\nauthor: TestAuthor\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void hasAuthorDash() {
        String headerString = "\nauthor: Test-Author\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("Test-Author"));
    }

    @Test
    public void hasAuthorNumbers() {
        String headerString = "\nauthor: TestAuthor123\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor123"));
    }

    @Test
    public void hasAuthorWithComment() {
        String headerString = "\nauthor: TestAuthor #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void hasAuthorMultipleWhitespaces() {
        String headerString = "\nauthor:   TestAuthor  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void hasAuthorEmpty() {
        String headerString = "\nauthor: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor() == null);
    }
}
