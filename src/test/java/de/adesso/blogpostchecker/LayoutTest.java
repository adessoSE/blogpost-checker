package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class LayoutTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\nlayout: [post, post-xml]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithLayoutContainsMultipleWhitespaces() {
        String headerString = "\nlayout:   [post, post-xml]  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithLayoutContainsComment() {
        String headerString = "\nlayout: [post, post-xml] #Don't change this\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithLayoutDoesNotMatchLayoutString() {
        String headerString = "\nlayout: [post post-xml]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithLayoutEmpty() {
        String headerString = "\nlayout: []\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithLayoutNotInBrackets() {
        String headerString = "\nlayout: post, post-xml\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithLayoutEmptyAndNotInBrackets() {
        String headerString = "\nlayout: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }
}
