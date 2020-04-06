package de.adesso.blogpostchecker;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BlogpostCheckerApplicationTests {

    @Autowired
    private FileAnalyzer fileAnalyzer;

    /* * * * * * * * * * *
     *    Categories     *
     * * * * * * * * * * */

    @Test
    public void testHasCategories() {
        String headerString = "\ncategories: [TestCategory]\n";
        System.out.println(fileAnalyzer);
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void testHasCategoriesWithComment() {
        String headerString = "\ncategories: [TestCategory] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void testHasCategoriesMultipleWhitespaces() {
        String headerString = "\ncategories:   [TestCategory]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void testHasCategoriesNotInBrackets() {
        String headerString = "\ncategories: TestCategory \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories() == null);
    }

    @Test
    public void testHasCategoriesEmpty() {
        String headerString = "\ncategories: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    @Test
    public void testHasCategoriesEmptyNotInBrackets() {
        String headerString = "\ncategories: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getCategories().equals("TestCategory"));
    }

    /* * * * * * * * * * *
     *        Tags       *
     * * * * * * * * * * */

    @Test
    public void testHasTags() {
        String headerString = "\ntags: [TestTag]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void testHasTagsMoreTags() {
        String headerString = "\ntags: [TestTag1, TestTag2]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag1, TestTag2"));
    }

    @Test
    public void testHasTagsWithComment() {
        String headerString = "\ntags: [TestTag] #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void testHasTagsMultipleWhitespaces() {
        String headerString = "\ntags:   [TestTag]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags().equals("TestTag"));
    }

    @Test
    public void testHasTagsNotInBrackets() {
        String headerString = "\ntags: TestTag \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void testHasTagsEmpty() {
        String headerString = "\ntags: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    @Test
    public void testHasTagsEmptyNotInBrackets() {
        String headerString = "\ntags: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTags() == null);
    }

    /* * * * * * * * * * *
     *      Author       *
     * * * * * * * * * * */

    @Test
    public void testHasAuthor() {
        String headerString = "\nauthor: TestAuthor\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void testHasAuthorDash() {
        String headerString = "\nauthor: Test-Author\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("Test-Author"));
    }

    @Test
    public void testHasAuthorNumbers() {
        String headerString = "\nauthor: TestAuthor123\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor123"));
    }

    @Test
    public void testHasAuthorWithComment() {
        String headerString = "\nauthor: TestAuthor #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void testHasAuthorMultipleWhitespaces() {
        String headerString = "\nauthor:   TestAuthor  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor().equals("TestAuthor"));
    }

    @Test
    public void testHasAuthorEmpty() {
        String headerString = "\nauthor: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getAuthor() == null);
    }

    /* * * * * * * * * * *
     *       Title       *
     * * * * * * * * * * */

    @Test
    public void testHasTitle() {
        String headerString = "\ntitle: \"TestTitle\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void testHasTitleMoreWords() {
        String headerString = "\ntitle: \"Test Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test Title"));
    }

    @Test
    public void testHasTitleNumbers() {
        String headerString = "\ntitle: \"TestTitle 123\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title 123"));
    }

    @Test
    public void testHasTitleAnd() {
        String headerString = "\ntitle: \"TestTitle &\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title &"));
    }

    @Test
    public void testHasTitleDash() {
        String headerString = "\ntitle: \"Test-Title\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("Test-Title"));
    }

    @Test
    public void testHasTitleWithComment() {
        String headerString = "\ntitle: \"TestTitle\" #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void testHasTitleMultipleWhitespaces() {
        String headerString = "\ntitle:   \"TestTitle\"  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle().equals("TestTitle"));
    }

    @Test
    public void testHasTitleNotInQuotes() {
        String headerString = "\ntitle: TestTitle \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void testHasTitleEmpty() {
        String headerString = "\ntitle: \"\"\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    @Test
    public void testHasTitleEmptyNotInQuotes() {
        String headerString = "\ntitle: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getTitle() == null);
    }

    /* * * * * * * * * * *
     *      Layout       *
     * * * * * * * * * * */

    @Test
    public void testHasLayout() {
        String headerString = "\nlayout: [post, post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void testHasLayoutMultipleWhitespaces() {
        String headerString = "\nlayout:   [post, post-xml]  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void testHasLayoutWithComment() {
        String headerString = "\nlayout: [post, post-xml] #Don't change this\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout().equals("post, post-xml"));
    }

    @Test
    public void testHasLayoutDoesNotMatch() {
        String headerString = "\nlayout: [post post-xml]\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void testHasLayoutEmpty() {
        String headerString = "\nlayout: []\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void testHasLayoutNotInBrackets() {
        String headerString = "\nlayout: post, post-xml\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    @Test
    public void testHasLayoutEmptyNotInBrackets() {
        String headerString = "\nlayout: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getLayout() == null);
    }

    /* * * * * * * * * * *
     *        Date       *
     * * * * * * * * * * */

    @Test
    public void testHasDate() {
        String headerString = "\ndate: 2020-03-20 15:00\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void testHasDateWithComment() {
        String headerString = "\ndate: 2020-03-20 15:00 #Only One allowed\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void testHasDateMultipleWhitespaces() {
        String headerString = "\ndate:   2020-03-20 15:00  \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate().equals("2020-03-20 15:00"));
    }

    @Test
    public void testHasDateEmpty() {
        String headerString = "\ndate: \n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }

    @Test
    public void testHasDateDoesNotMatch() {
        String headerString = "\ndate: 3768248\n";
        PostHeader postHeader = fileAnalyzer.getHeaderFromString(headerString);
        Assertions.assertThat(postHeader.getDate() == null);
    }
}
