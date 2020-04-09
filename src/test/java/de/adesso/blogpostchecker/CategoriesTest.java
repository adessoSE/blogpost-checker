package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoriesTest extends BaseTest {
    
    @Test
    public void getHeaderFromStringShouldPass() {
        String headerString = "\ncategories: [TestCategory]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithCategoriesContainsComment() {
        String headerString = "\ncategories: [TestCategory] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithCategoriesContainsMultipleWhitespaces() {
        String headerString = "\ncategories:   [TestCategory]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithCategoriesNotInBrackets() {
        String headerString = "\ncategories: TestCategory \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithCategoriesEmpty() {
        String headerString = "\ncategories: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithCategoriesEmptyAndNotInBrackets() {
        String headerString = "\ncategories: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories() == null);
    }
}
