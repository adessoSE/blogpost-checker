package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        System.out.println(fileAnalyzer);
        String headerString = "\nauthor: TestAuthor\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsDash() {
        String headerString = "\nauthor: Test-Author\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("Test-Author"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsNumbers() {
        String headerString = "\nauthor: TestAuthor123\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor123"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithWithNameContainsComment() {
        String headerString = "\nauthor: TestAuthor #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsMultipleWhitespaces() {
        String headerString = "\nauthor:   TestAuthor  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithNameEmpty() {
        String headerString = "\nauthor: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor() == null);
    }
}
