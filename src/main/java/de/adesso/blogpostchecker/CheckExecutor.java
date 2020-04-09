package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);

    public void executeChecks(PostMetadata postMetadata, String authors) {
        checkCategoryNotEmpty(postMetadata);
        checkCategoryOnlyOneEntry(postMetadata);
        checkTagsNotEmpty(postMetadata);
        checkAuthorNotEmpty(postMetadata);
        checkAuthorInYml(postMetadata, authors);
        checkTitleNotEmpty(postMetadata);
        checkLayoutCorrect(postMetadata);
        checkDateMatchesFormat(postMetadata);
    }

    private void checkCategoryNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getCategories())) {
            LOGGER.info("Categories not empty checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "No category found. Exactly one category expected.", 30);
        }
    }

    private void checkCategoryOnlyOneEntry(PostMetadata metadata) {
        if (!metadata.getCategories().contains(",")) {
            LOGGER.info("Categories checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Two or more categories found. Exactly one category expected", 31);
        }
    }

    private void checkTagsNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTags())) {
            LOGGER.info("Tags checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The tags are empty. One or more tags expected.", 32);
        }
    }

    private void checkAuthorNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getAuthor())) {
            LOGGER.info("Author checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "No author found. Exactly one author expected.", 33);
        }
    }

    private void checkAuthorInYml(PostMetadata metadata, String authors) {
        if (authors.contains(metadata.getAuthor())) {
            LOGGER.info("Author is in authors.yml.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The selected author was not found in authors.yml. Make sure author exists and is spelled correctly in the blogpost.", 34);
        }
    }

    private void checkTitleNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTitle())) {
            LOGGER.info("Title checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Blogpost title is missing. Provide a title.", 35);
        }
    }

    private void checkLayoutCorrect(PostMetadata metadata) {
        if (checkAttribute(metadata.getLayout()) && metadata.getLayout().equals("post, post-xml")) {
            LOGGER.info("Layout checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "'Layout'does not contain 'post, post-xml'. Make sure to use 'layout: [post, post-xml]'", 36);
        }
    }

    private void checkDateMatchesFormat(PostMetadata metadata) {
        if (checkAttribute(metadata.getDate()) && metadata.getDate().matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}")) {
            LOGGER.info("Date checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Date format error. Adapt to accepted pattern YYYY-MM-DD HH:mm", 37);
        }
    }

    private boolean checkAttribute(String attribute) {
        return attribute != null && !attribute.equals("");
    }
}
