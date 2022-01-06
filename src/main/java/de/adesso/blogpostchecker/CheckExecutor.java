package de.adesso.blogpostchecker;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckExecutor.class);
    private final Map<String, String> errors = new HashMap<>();

    private final ConfigService configService;

    @Autowired
    public CheckExecutor(ConfigService configService) {
        this.configService = configService;
    }

    public void executeChecks(PostMetadata postMetadata, List<Author> authors) {
        checkPostCategoryNotEmpty(postMetadata);
        checkPostCategoryOnlyOneEntry(postMetadata);
        checkPostTagsNotEmpty(postMetadata);
        checkPostAuthorNotEmpty(postMetadata);
        checkPostTitleNotEmpty(postMetadata);
        checkPostLayoutCorrect(postMetadata);
        checkPostDateMatchesFormat(postMetadata);

        authors.forEach(author -> {
            checkAuthorFirstNameNotEmpty(author);
            checkAuthorLastNameNotEmpty(author);
            checkAuthorGithubUsernameNotEmpty(author);
            checkAuthorEmailNotEmpty(author);
            checkAuthorEmailCorrectFormat(author);
            checkAuthorBioNotEmpty(author);
            checkAuthorAvatarUrlNotEmpty(author);
            checkAuthorGithubNotEmpty(author);
        });

        if (!errors.isEmpty()) {
            String message = errors.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining(",\n"));

            LOGGER.info("There were errors in the blog post:\n{}", message);

            HttpHeaders headers = createHeaders(configService.getUSERNAME(), configService.getTOKEN());
            headers.add("Accept", "application/vnd.github.v3+json");

            try {
                LOGGER.info("try for user {}", configService.getUSERNAME());
                ResponseEntity<Object> response = new RestTemplate().exchange(
                        "https://api.github.com/repos/adessoAG/devblog/issues/" + configService.getPR_NUMBER() + "/comments",
                        HttpMethod.POST,
                        new HttpEntity<>(new FeedbackComment(message), headers),
                        Object.class);
                if (HttpStatus.CREATED.equals(response.getStatusCode())) {
                    LOGGER.info("Successfully posted errors as PR comment.");
                } else {
                    LOGGER.error("Error during posting errors as PR comment: {}", response.getStatusCode());
                }
            } catch (HttpClientErrorException e) {
                System.out.println(e.getResponseBodyAsString());
                LOGGER.error("Error during posting errors as PR comment: {} ({})", e.getStatusCode(), e.getMessage());
            }
            ExitBlogpostChecker.exit(LOGGER, "There were errors in the blog post", 318);
        }
    }

    private void checkPostCategoryNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getCategories())) {
            LOGGER.info("Categories not empty checked, value: \"{}\"", metadata.getCategories());
        } else {
            errors.put("categories", "No category found. Exactly one category expected.");
        }
    }

    private void checkPostCategoryOnlyOneEntry(PostMetadata metadata) {
        if (!metadata.getCategories().contains(",")) {
            LOGGER.info("Categories checked");
        } else {
            errors.put("categories", "Two or more categories found. Exactly one category expected.");
        }
    }

    private void checkPostTagsNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTags())) {
            LOGGER.info("Tags checked, value: \"{}\"", metadata.getTags());
        } else {
            errors.put("tags", "The tags are empty. One or more tags expected.");
        }
    }

    private void checkPostAuthorNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getAuthor())) {
            LOGGER.info("Author checked, value: \"{}\"", metadata.getAuthor());
        } else {
            errors.put("author_ids", "No author found. One or more authors expected.");
        }
    }

    private void checkPostTitleNotEmpty(PostMetadata metadata) {
        if (checkAttribute(metadata.getTitle())) {
            LOGGER.info("Title checked, value: \"{}\"", metadata.getTitle());
        } else {
            errors.put("title", "Blogpost title is missing. Provide a title.");
        }
    }

    private void checkPostLayoutCorrect(PostMetadata metadata) {
        if (checkAttribute(metadata.getLayout()) && metadata.getLayout().equals("post, post-xml")) {
            LOGGER.info("Layout checked, value: \"{}\"", metadata.getLayout());
        } else {
            errors.put("layout", "'Layout' does not contain 'post, post-xml'. Make sure to use 'layout: [post, post-xml]'");
        }
    }

    private void checkPostDateMatchesFormat(PostMetadata metadata) {
        if (dateMatchesPattern(metadata)) {
            LOGGER.info("Date checked, value: \"{}\"", metadata.getDate());
        } else {
            errors.put("date", "Date format error. Adapt to accepted pattern YYYY-MM-DD HH:mm");
        }
    }

    private void checkAuthorFirstNameNotEmpty(Author author) {
        if (checkAttribute(author.getFirstName())) {
            LOGGER.info("Author first name checked, value: \"{}\"", author.getFirstName());
        } else {
            errors.put("author_ids", "Author first name is missing for author " + author.getAuthorNickname() + ". Provide a first name.");
        }
    }

    private void checkAuthorLastNameNotEmpty(Author author) {
        if (checkAttribute(author.getLastName())) {
            LOGGER.info("Author last name checked, value: \"{}\"", author.getLastName());
        } else {
            errors.put("author_ids", "Author last name is missing for author " + author.getAuthorNickname() + ". Provide a last name.");
        }
    }

    private void checkAuthorGithubUsernameNotEmpty(Author author) {
        if (checkAttribute(author.getGithubUsername())) {
            LOGGER.info("Author github username checked, value: \"{}\"", author.getGithubUsername());
        } else {
            errors.put("author_ids", "Author github username is missing for author " + author.getAuthorNickname() + ". Provide a github username.");
        }
    }

    private void checkAuthorEmailNotEmpty(Author author) {
        if (checkAttribute(author.getEmail())) {
            LOGGER.info("Author email checked, value: \"{}\"", author.getEmail());
        } else {
            errors.put("author_ids", "Author email is missing for author " + author.getAuthorNickname() + ". Provide a email.");
        }
    }

    private void checkAuthorEmailCorrectFormat(Author author) {
        if (emailMatchesEmailPattern(author)) {
            LOGGER.info("Author email matches pattern checked");
        } else {
            errors.put("author_ids", "Email format error for author " + author.getAuthorNickname() + ". Adapt to accepted pattern ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$. Provide a last name.");
        }
    }

    private void checkAuthorBioNotEmpty(Author author) {
        if (checkAttribute(author.getBio())) {
            LOGGER.info("Author bio checked, value: \"{}\"", author.getBio());
        } else {
            errors.put("author_ids", "Author bio is missing for author " + author.getAuthorNickname() + ". Provide a bio.");
        }
    }

    private void checkAuthorAvatarUrlNotEmpty(Author author) {
        if (checkAttribute(author.getAvatarUrl())) {
            LOGGER.info("Author avatar url checked, value: \"{}\"", author.getAvatarUrl());
        } else {
            errors.put("author_ids", "Author avatar url is missing for author " + author.getAuthorNickname() + ". Provide an avatar url.");
        }
    }

    private void checkAuthorGithubNotEmpty(Author author) {
        if (checkAttribute(author.getGithub())) {
            LOGGER.info("Author github checked, value: \"{}\"", author.getGithub());
        } else {
            errors.put("author_ids", "Author github is missing for author " + author.getAuthorNickname() + ". Provide a github link.");
        }
    }

    private boolean checkAttribute(String attribute) {
        return attribute != null && !attribute.equals("");
    }

    private boolean dateMatchesPattern(PostMetadata metadata) {
        return checkAttribute(metadata.getDate()) && metadata.getDate().matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}");
    }

    private boolean emailMatchesEmailPattern(Author author) {
        return author.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }
}
