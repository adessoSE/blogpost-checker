package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);

    public void executeChecks(PostHeader postHeader, Author author) {
        checkPostCategoryOnlyOneEntry(postHeader);
        checkPostTagsNotEmpty(postHeader);
        checkPostAuthorNotEmpty(postHeader);
        checkPostTitleNotEmpty(postHeader);
        checkPostLayoutCorrect(postHeader);
        checkPostDateMatchesFormat(postHeader);

        checkAuthorFirstNameNotEmpty(author);
        checkAuthorLastNameNotEmpty(author);
        checkAuthorGithubUsernameNotEmpty(author);
        checkAuthorEmailNotEmpty(author);
        checkAuthorEmailCorrectFormat(author);
        checkAuthorBioNotEmpty(author);
        checkAuthorAvatarUrlNotEmpty(author);
        checkAuthorGithubNotEmpty(author);
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

    private void checkAuthorLastNameNotEmpty(Author author) {
        if (checkAttribute(author.getLastName())) {
            LOGGER.info("Author last name checked");
        } else {
            LOGGER.error("Author last name is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(311);
        }
    }

    private void checkAuthorGithubUsernameNotEmpty(Author author) {
        if (checkAttribute(author.getGithubUsername())) {
            LOGGER.info("Author github username checked");
        } else {
            LOGGER.error("Author github username is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(312);
        }
    }

    private void checkAuthorEmailNotEmpty(Author author) {
        if (checkAttribute(author.getEmail())) {
            LOGGER.info("Author email checked");
        } else {
            LOGGER.error("Author email is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(313);
        }
    }

    private void checkAuthorEmailCorrectFormat(Author author) {
        if (author.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            LOGGER.info("Author email matches pattern checked");
        } else {
            LOGGER.error("Author email does not match pattern.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(314);
        }
    }

    private void checkAuthorBioNotEmpty(Author author) {
        if (checkAttribute(author.getBio())) {
            LOGGER.info("Author bio checked");
        } else {
            LOGGER.error("Author bio is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(315);
        }
    }

    private void checkAuthorAvatarUrlNotEmpty(Author author) {
        if (checkAttribute(author.getAvatarUrl())) {
            LOGGER.info("Author avatar url checked");
        } else {
            LOGGER.error("Author avatar url is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(316);
        }
    }

    private void checkAuthorGithubNotEmpty(Author author) {
        if (checkAttribute(author.getGithub())) {
            LOGGER.info("Author github checked");
        } else {
            LOGGER.error("Author github is empty.");
            LOGGER.error("Exiting BlogpostChecker.");
            System.exit(317);
        }
    }

    private boolean checkAttribute(String attribute) {
        return attribute != null && !attribute.equals("");
    }

}
