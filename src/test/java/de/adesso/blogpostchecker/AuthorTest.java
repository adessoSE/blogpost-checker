package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorTest extends BaseTest {

    @Test
    public void getHeaderFromStringShouldPass() {
        String headerString = "\nauthor: TestAuthor\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithNameContainsDash() {
        String headerString = "\nauthor: Test-Author\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("Test-Author"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithNameContainsNumbers() {
        String headerString = "\nauthor: TestAuthor123\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor123"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithWithNameContainsComment() {
        String headerString = "\nauthor: TestAuthor #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithNameContainsMultipleWhitespaces() {
        String headerString = "\nauthor:   TestAuthor  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithNameEmpty() {
        String headerString = "\nauthor: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor() == null);
    }
}
