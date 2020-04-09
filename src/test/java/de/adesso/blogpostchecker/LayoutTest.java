package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class LayoutTest extends BaseTest {

    @Test
    public void getHeaderFromStringShouldPass() {
        String headerString = "\nlayout: [post, post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithLayoutContainsMultipleWhitespaces() {
        String headerString = "\nlayout:   [post, post-xml]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithLayoutContainsComment() {
        String headerString = "\nlayout: [post, post-xml] #Don't change this\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithLayoutDoesNotMatchLayoutString() {
        String headerString = "\nlayout: [post post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithLayoutEmpty() {
        String headerString = "\nlayout: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithLayoutNotInBrackets() {
        String headerString = "\nlayout: post, post-xml\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithLayoutEmptyAndNotInBrackets() {
        String headerString = "\nlayout: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }
}
