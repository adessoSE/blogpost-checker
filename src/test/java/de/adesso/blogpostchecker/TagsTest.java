package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagsTest extends BaseTest {
    
    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\ntags: [TestTag]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTagsContainsMoreTags() {
        String headerString = "\ntags: [TestTag1, TestTag2]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag1, TestTag2"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTagsContainsComment() {
        String headerString = "\ntags: [TestTag] #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithTagsContainsMultipleWhitespaces() {
        String headerString = "\ntags:   [TestTag]  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTagsNotInBrackets() {
        String headerString = "\ntags: TestTag \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTagsEmpty() {
        String headerString = "\ntags: []\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithTagsEmptyAndNotInBrackets() {
        String headerString = "\ntags: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }
}
