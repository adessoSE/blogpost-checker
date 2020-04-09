package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoriesTest extends BaseTest {
    
    @Test
    public void hasCategories() {
        String headerString = "\ncategories: [TestCategory]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void hasCategoriesWithComment() {
        String headerString = "\ncategories: [TestCategory] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void hasCategoriesMultipleWhitespaces() {
        String headerString = "\ncategories:   [TestCategory]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void hasCategoriesNotInBrackets() {
        String headerString = "\ncategories: TestCategory \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories() == null);
    }

    @Test
    public void hasCategoriesEmpty() {
        String headerString = "\ncategories: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void hasCategoriesEmptyNotInBrackets() {
        String headerString = "\ncategories: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories() == null);
    }
}
