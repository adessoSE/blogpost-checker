package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);

    public void executeChecks(PostMetadata postMetadata, Author author) {
        checkPostCategoryOnlyOneEntry(postMetadata);
        checkPostCategoryOnlyOneEntry(postMetadata);
        checkPostTagsNotEmpty(postMetadata);
        checkPostAuthorNotEmpty(postMetadata);
        checkPostTitleNotEmpty(postMetadata);
        checkPostLayoutCorrect(postMetadata);
        checkPostDateMatchesFormat(postMetadata);

        checkAuthorFirstNameNotEmpty(author);
        checkAuthorLastNameNotEmpty(author);
        checkAuthorGithubUsernameNotEmpty(author);
        checkAuthorEmailNotEmpty(author);
        checkAuthorEmailCorrectFormat(author);
        checkAuthorBioNotEmpty(author);
        checkAuthorAvatarUrlNotEmpty(author);
        checkAuthorGithubNotEmpty(author);
    }

    private void checkPostCategoryNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getCategories())) {
            LOGGER.info("Categories not empty checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "No category found. Exactly one category expected.", 300);
        }
    }

    private void checkPostCategoryOnlyOneEntry(PostMetadata metadata) {
        if (!metadata.getCategories().contains(",")) {
            LOGGER.info("Categories checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Two or more categories found. Exactly one category expected", 301);
        }
    }

    private void checkPostTagsNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTags())) {
            LOGGER.info("Tags checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The tags are empty. One or more tags expected.", 302);
        }
    }

    private void checkPostAuthorNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getAuthor())) {
            LOGGER.info("Author checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "No author found. Exactly one author expected.", 303);
        }
    }

    private void checkPostAuthorInYml(PostMetadata metadata, String authors) {
        if (authors.contains(metadata.getAuthor())) {
            LOGGER.info("Author is in authors.yml.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "The selected author was not found in authors.yml. Make sure author exists and is spelled correctly in the blogpost.", 304);
        }
    }

    private void checkPostTitleNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTitle())) {
            LOGGER.info("Title checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Blogpost title is missing. Provide a title.", 305);
        }
    }

    private void checkPostLayoutCorrect(PostMetadata metadata) {
        if (checkAttribute(metadata.getLayout()) && metadata.getLayout().equals("post, post-xml")) {
            LOGGER.info("Layout checked.");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "'Layout'does not contain 'post, post-xml'. Make sure to use 'layout: [post, post-xml]'", 306);
        }
    }

    private void checkPostDateMatchesFormat(PostMetadata metadata) {
        if (dateMatchesPattern(metadata)) {
            LOGGER.info("Date checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Date format error. Adapt to accepted pattern YYYY-MM-DD HH:mm", 307);
        }
    }

    private void checkAuthorFirstNameNotEmpty(Author author) {
        if (checkAttribute(author.getFirstName())) {
            LOGGER.info("Author first name checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author first name is missing. Provide a first name.", 310);
        }
    }

    private void checkAuthorLastNameNotEmpty(Author author) {
        if (checkAttribute(author.getLastName())) {
            LOGGER.info("Author last name checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author last name is missing. Provide a last name.", 311);
        }
    }

    private void checkAuthorGithubUsernameNotEmpty(Author author) {
        if (checkAttribute(author.getGithubUsername())) {
            LOGGER.info("Author github username checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author github username is missing. Provide a github username.", 312);
        }
    }

    private void checkAuthorEmailNotEmpty(Author author) {
        if (checkAttribute(author.getEmail())) {
            LOGGER.info("Author email checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author email is missing. Provide a email.", 313);
        }
    }

    private void checkAuthorEmailCorrectFormat(Author author) {
        if (author.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            LOGGER.info("Author email matches pattern checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Email format error. Adapt to accepted pattern ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$.", 314);
        }
    }

    private void checkAuthorBioNotEmpty(Author author) {
        if (checkAttribute(author.getBio())) {
            LOGGER.info("Author bio checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author bio is missing. Provide a bio.", 315);
        }
    }

    private void checkAuthorAvatarUrlNotEmpty(Author author) {
        if (checkAttribute(author.getAvatarUrl())) {
            LOGGER.info("Author avatar url checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author avatar url is missing. Provide an avatar url.", 316);
        }
    }

    private void checkAuthorGithubNotEmpty(Author author) {
        if (checkAttribute(author.getGithub())) {
            LOGGER.info("Author github checked");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "Author github is missing. Provide a github link.", 317);
        }
    }

    private boolean checkAttribute(String attribute) {
        return attribute != null && !attribute.equals("");
    }

    private boolean dateMatchesPattern(PostMetadata metadata) {
        return checkAttribute(metadata.getDate()) && metadata.getDate().matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}");
    }
}
