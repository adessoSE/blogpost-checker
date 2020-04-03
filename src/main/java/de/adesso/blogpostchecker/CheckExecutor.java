package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);

    public void executeChecks(PostHeader postHeader, String authors) {
        checkCategoryOnlyOneEntry(postHeader);
        checkTagsNotEmpty(postHeader);
        checkAuthorNotEmpty(postHeader);
        checkAuthorInYml(postHeader, authors);
        checkTitleNotEmpty(postHeader);
        checkLayoutCorrect(postHeader);
        checkDateMatchesFormat(postHeader);
    }

    private void checkCategoryOnlyOneEntry(PostHeader header) {
        if (header.getCategories() != null && !header.getCategories().equals("") && !header.getCategories().contains(",")) {
            LOGGER.info("Layout checked");
        } else {
            LOGGER.error("Layout does not match.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(30);
        }
    }

    private void checkTagsNotEmpty(PostHeader header) {
        if (header.getTags() != null && !header.getTags().equals("")) {
            LOGGER.info("Tags checked");
        } else {
            LOGGER.error("Checks are empty");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(31);
        }
    }

    private void checkAuthorNotEmpty(PostHeader header) {
        if (header.getAuthor() != null && !header.getAuthor().equals("")) {
            LOGGER.info("Author checked");
        } else {
            LOGGER.error("Author is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(32);
        }
    }

    private void checkAuthorInYml(PostHeader header, String authors) {
        if (authors.contains(header.getAuthor())) {
            LOGGER.info("Author is in authors.yml.");
        } else {
            LOGGER.error("Author not in authors.yml checked.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(33);
        }
    }

    private void checkTitleNotEmpty(PostHeader header) {
        if (header.getTitle() != null && !header.getTitle().equals("")) {
            LOGGER.info("Title checked.");
        } else {
            LOGGER.error("Title is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(34);
        }
    }

    private void checkLayoutCorrect(PostHeader header) {
        if (header.getLayout().equals("post, post-xml")) {
            LOGGER.info("Layout checked.");
        } else {
            LOGGER.error("Layout does not match 'post post-xml'.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(35);
        }
    }

    private void checkDateMatchesFormat(PostHeader header) {
        if (header.getDate().matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}")) {
            LOGGER.info("Date checked");
        } else {
            LOGGER.error("Date does not matched accepted pattern YYYY-MM-DD HH:mm.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(36);
        }
    }
}
