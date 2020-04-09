package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagsTest extends BaseTest {
    
    @Test
    public void getHeaderFromStringShouldPass() {
        String headerString = "\ntags: [TestTag]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTagsContainsMoreTags() {
        String headerString = "\ntags: [TestTag1, TestTag2]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag1, TestTag2"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTagsContainsComment() {
        String headerString = "\ntags: [TestTag] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTagsContainsMultipleWhitespaces() {
        String headerString = "\ntags:   [TestTag]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithTagsNotInBrackets() {
        String headerString = "\ntags: TestTag \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithTagsEmpty() {
        String headerString = "\ntags: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithTagsEmptyAndNotInBrackets() {
        String headerString = "\ntags: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }
}
