package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TitleTest extends BaseTest {

    @Test
    public void hasTitle() {
        String headerString = "\ntitle: \"TestTitle\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void hasTitleMoreWords() {
        String headerString = "\ntitle: \"Test Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test Title"));
    }

    @Test
    public void hasTitleNumbers() {
        String headerString = "\ntitle: \"TestTitle 123\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title 123"));
    }

    @Test
    public void hasTitleAnd() {
        String headerString = "\ntitle: \"TestTitle &\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title &"));
    }

    @Test
    public void hasTitleDash() {
        String headerString = "\ntitle: \"Test-Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title"));
    }

    @Test
    public void hasTitleWithComment() {
        String headerString = "\ntitle: \"TestTitle\" #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void hasTitleMultipleWhitespaces() {
        String headerString = "\ntitle:   \"TestTitle\"  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void hasTitleNotInQuotes() {
        String headerString = "\ntitle: TestTitle \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void hasTitleEmpty() {
        String headerString = "\ntitle: \"\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void hasTitleEmptyNotInQuotes() {
        String headerString = "\ntitle: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }
}
