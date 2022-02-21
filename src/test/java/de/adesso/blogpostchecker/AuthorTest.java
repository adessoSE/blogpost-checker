package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\nauthor_ids: [TestAuthor]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("TestAuthor");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassMultipleAuthors() {
        String headerString = "\nauthor_ids: [TestAuthor, TestAuthor2]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("TestAuthor, TestAuthor2");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsDash() {
        String headerString = "\nauthor_ids: [Test-Author]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("Test-Author");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsNumbers() {
        String headerString = "\nauthor_ids: [TestAuthor123]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("TestAuthor123");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithWithNameContainsComment() {
        String headerString = "\nauthor_ids: [TestAuthor] #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("TestAuthor");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithNameContainsMultipleWhitespaces() {
        String headerString = "\nauthor_ids:   [TestAuthor]  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isEqualTo("TestAuthor");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithNameEmpty() {
        String headerString = "\nauthor_ids: []\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getAuthor()).isNull();
    }
}
