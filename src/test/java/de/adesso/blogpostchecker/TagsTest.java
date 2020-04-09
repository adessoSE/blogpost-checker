package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagsTest extends BaseTest {
    
    @Test
    public void hasTags() {
        String headerString = "\ntags: [TestTag]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void hasTagsMoreTags() {
        String headerString = "\ntags: [TestTag1, TestTag2]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag1, TestTag2"));
    }

    @Test
    public void hasTagsWithComment() {
        String headerString = "\ntags: [TestTag] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void hasTagsMultipleWhitespaces() {
        String headerString = "\ntags:   [TestTag]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void hasTagsNotInBrackets() {
        String headerString = "\ntags: TestTag \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void hasTagsEmpty() {
        String headerString = "\ntags: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void hasTagsEmptyNotInBrackets() {
        String headerString = "\ntags: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }
}
