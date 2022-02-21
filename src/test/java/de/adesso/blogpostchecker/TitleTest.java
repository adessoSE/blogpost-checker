package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TitleTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\ntitle: \"TestTitle\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        System.out.println(postHeader);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("TestTitle");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassUsingSingleQuotes() {
        String headerString = "\ntitle: 'TestTitle'\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("TestTitle");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsMoreWords() {
        String headerString = "\ntitle: \"Test Title\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("Test Title");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsNumbers() {
        String headerString = "\ntitle: \"Test-Title 123\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("Test-Title 123");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsAndSymbol() {
        String headerString = "\ntitle: \"Test-Title &\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("Test-Title &");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsDash() {
        String headerString = "\ntitle: \"Test-Title\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("Test-Title");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsComment() {
        String headerString = "\ntitle: \"TestTitle\" #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("TestTitle");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTitleContainsMultipleWhitespaces() {
        String headerString = "\ntitle:   \"TestTitle\"  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isEqualTo("TestTitle");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTitleNotInQuotes() {
        String headerString = "\ntitle: TestTitle \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isNull();
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTitleEmpty() {
        String headerString = "\ntitle: \"\"\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isNull();
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTitleEmptyAndNotInQuotes() {
        String headerString = "\ntitle: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTitle()).isNull();
    }
}
