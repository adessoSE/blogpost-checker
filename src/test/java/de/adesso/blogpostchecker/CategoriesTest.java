package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoriesTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\ncategories: [TestCategory]\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isEqualTo("TestCategory");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithCategoriesContainsComment() {
        String headerString = "\ncategories: [TestCategory] #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isEqualTo("TestCategory");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithCategoriesContainsMultipleWhitespaces() {
        String headerString = "\ncategories:   [TestCategory]  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isEqualTo("TestCategory");
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithCategoriesNotInBrackets() {
        String headerString = "\ncategories: TestCategory \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isNull();
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithCategoriesEmpty() {
        String headerString = "\ncategories: []\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isNull();
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithCategoriesEmptyAndNotInBrackets() {
        String headerString = "\ncategories: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getCategories()).isNull();
    }
}
