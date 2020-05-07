package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTest extends BaseTest {

    @Test
    public void extractMetadataFromStringUsingRegexShouldPass() {
        String headerString = "\ndate: 2020-03-20 15:00\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithDateContainsComment() {
        String headerString = "\ndate: 2020-03-20 15:00 #Only One allowed\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldPassWithDateContainsMultipleWhitespaces() {
        String headerString = "\ndate:   2020-03-20 15:00  \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithDateEmpty() {
        String headerString = "\ndate: \n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }

    @Test
    public void extractMetadataFromStringUsingRegexShouldFailWithDateDoesNotMatchDatePattern() {
        String headerString = "\ndate: 3768248\n";
        PostMetadata postHeader = fileAnalyzer.extractMetadataFromStringUsingRegex(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }
}
