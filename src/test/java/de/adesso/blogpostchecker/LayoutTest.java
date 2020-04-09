package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class LayoutTest extends BaseTest {

    @Test
    public void hasLayout() {
        String headerString = "\nlayout: [post, post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void hasLayoutMultipleWhitespaces() {
        String headerString = "\nlayout:   [post, post-xml]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void hasLayoutWithComment() {
        String headerString = "\nlayout: [post, post-xml] #Don't change this\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void hasLayoutDoesNotMatch() {
        String headerString = "\nlayout: [post post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void hasLayoutEmpty() {
        String headerString = "\nlayout: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void hasLayoutNotInBrackets() {
        String headerString = "\nlayout: post, post-xml\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void hasLayoutEmptyNotInBrackets() {
        String headerString = "\nlayout: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }
}
