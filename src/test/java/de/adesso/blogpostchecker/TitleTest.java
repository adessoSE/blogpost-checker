package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TitleTest extends BaseTest {

    @Test
    public void getHeaderFromStringShouldPass() {
        String headerString = "\ntitle: \"TestTitle\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsMoreWords() {
        String headerString = "\ntitle: \"Test Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test Title"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsNumbers() {
        String headerString = "\ntitle: \"TestTitle 123\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title 123"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsAndSymbol() {
        String headerString = "\ntitle: \"TestTitle &\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title &"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsDash() {
        String headerString = "\ntitle: \"Test-Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsComment() {
        String headerString = "\ntitle: \"TestTitle\" #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void getHeaderFromStringShouldPassWithTitleContainsMultipleWhitespaces() {
        String headerString = "\ntitle:   \"TestTitle\"  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void getHeaderFromStringShouldFailWithTitleNotInQuotes() {
        String headerString = "\ntitle: TestTitle \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithTitleEmpty() {
        String headerString = "\ntitle: \"\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void getHeaderFromStringShouldFailWithTitleEmptyAndNotInQuotes() {
        String headerString = "\ntitle: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }
}
