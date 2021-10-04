package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @Value("#{environment.REPOSITORY_REMOTE_URL ?: null}")
    private String REPOSITORY_REMOTE_URL;

    @Value("#{environment.REPOSITORY_BRANCH_NAME ?: null}")
    private String REPOSITORY_BRANCH_NAME;

    @Value("#{environment.LOCAL_REPO_PATH ?: '${repository.local.path}'}")
    private String LOCAL_REPO_PATH;

    public void checkConfiguration() {
        checkRemoteRepoUrl();
        checkRepositoryBranchName();
    }

    public String getREPOSITORY_BRANCH_NAME() {
        return REPOSITORY_BRANCH_NAME;
    }

    public String getREPOSITORY_REMOTE_URL() {
        return REPOSITORY_REMOTE_URL;
    }

    public String getLOCAL_REPO_PATH() {
        return LOCAL_REPO_PATH;
    }

    private void checkRemoteRepoUrl() {
        if (REPOSITORY_REMOTE_URL == null) {
            logAndExitVariableNotFound("REPOSITORY_REMOTE_URL",
                    "Please provide a repository url in the format of: https://github.com/<name-of-my-account>/devblog.",
                    10);
        } else {
            LOGGER.info("Environment variable provided: REPOSITORY_REMOTE_URL");
        }
    }

    private void checkRepositoryBranchName() {
        if (REPOSITORY_BRANCH_NAME == null) {
            logAndExitVariableNotFound("REPOSITORY_BRANCH_NAME", "Please provide the branch you want to check.", 11);
        } else {
            LOGGER.info("Environment variable provided: REPOSITORY_BRANCH_NAME");
        }
    }

    private void logAndExitVariableNotFound(String variable, String description, int exitCode) {
        ExitBlogpostChecker.exit(LOGGER, "Environment variable not provided: " + variable + ". " + description, exitCode);
    }
}
