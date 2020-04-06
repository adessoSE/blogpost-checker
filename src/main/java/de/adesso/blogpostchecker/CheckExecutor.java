package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);

    public void executeChecks(PostMetaData postMetaData, String authors) {
        checkCategoryNotEmpty(postMetaData);
        checkCategoryOnlyOneEntry(postMetaData);
        checkTagsNotEmpty(postMetaData);
        checkAuthorNotEmpty(postMetaData);
        checkAuthorInYml(postMetaData, authors);
        checkTitleNotEmpty(postMetaData);
        checkLayoutCorrect(postMetaData);
        checkDateMatchesFormat(postMetaData);
    }

    private void checkCategoryNotEmpty(PostMetaData metaData) {
        if (checkAttribute(metaData.getCategories())) {
            LOGGER.info("Categories not empty checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "THe categories does not match.", 30);
        }
    }

    private void checkCategoryOnlyOneEntry(PostMetaData metaData) {
        if (!metaData.getCategories().contains(",")) {
            LOGGER.info("Categories checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "There are two or more categories.", 31);
        }
    }

    private void checkTagsNotEmpty(PostMetaData metaData) {
        if (checkAttribute(metaData.getTags())) {
            LOGGER.info("Tags checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The tags are empty.", 32);
        }
    }

    private void checkAuthorNotEmpty(PostMetaData metaData) {
        if (checkAttribute(metaData.getAuthor())) {
            LOGGER.info("Author checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The author ist empty.", 33);
        }
    }

    private void checkAuthorInYml(PostMetaData metaData, String authors) {
        if (authors.contains(metaData.getAuthor())) {
            LOGGER.info("Author is in authors.yml.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "THe author not in authors.yml.", 34);
        }
    }

    private void checkTitleNotEmpty(PostMetaData metaData) {
        if (checkAttribute(metaData.getTitle())) {
            LOGGER.info("Title checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The title is empty.", 35);
        }
    }

    private void checkLayoutCorrect(PostMetaData metaData) {
        if (checkAttribute(metaData.getLayout()) && metaData.getLayout().equals("post, post-xml")) {
            LOGGER.info("Layout checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The layout does not match 'post, post-xml'.", 36);
        }
    }

    private void checkDateMatchesFormat(PostMetaData metaData) {
        if (checkAttribute(metaData.getDate()) && metaData.getDate().matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}")) {
            LOGGER.info("Date checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The date does not matched accepted pattern YYYY-MM-DD HH:mm..", 37);
        }
    }

    private boolean checkAttribute(String attribute) {
        return attribute != null && !attribute.equals("");
    }
}
