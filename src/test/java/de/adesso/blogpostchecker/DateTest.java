package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTest extends BaseTest {

    @Test
    public void hasDate() {
        String headerString = "\ndate: 2020-03-20 15:00\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void hasDateWithComment() {
        String headerString = "\ndate: 2020-03-20 15:00 #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void hasDateMultipleWhitespaces() {
        String headerString = "\ndate:   2020-03-20 15:00  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void hasDateEmpty() {
        String headerString = "\ndate: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }

    @Test
    public void hasDateDoesNotMatch() {
        String headerString = "\ndate: 3768248\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }
}
