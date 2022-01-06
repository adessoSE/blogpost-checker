package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @Value("#{environment.BASE_COMMIT ?: null}")
    private String BASE_COMMIT;

    @Value("#{environment.HEAD_COMMIT ?: null}")
    private String HEAD_COMMIT;

    @Value("#{environment.LOCAL_REPO_PATH ?: '${repository.local.path}'}")
    private String LOCAL_REPO_PATH;

    @Value("#{environment.PR_NUMBER ?: null}")
    private String PR_NUMBER;

    @Value("#{environment.USERNAME ?: null}")
    private String USERNAME;

    @Value("#{environment.TOKEN ?: null}")
    private String TOKEN;

    public void checkConfiguration() {
        checkHeadCommit();
        checkBaseCommit();
        checkLocalRepoPath();
        checkPrNumber();
        checkUsername();
        checkToken();
    }

    public String getBASE_COMMIT() {
        return BASE_COMMIT;
    }

    public String getHEAD_COMMIT() {
        return HEAD_COMMIT;
    }

    public String getLOCAL_REPO_PATH() {
        return LOCAL_REPO_PATH;
    }

    public String getPR_NUMBER() {
        return PR_NUMBER;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    private void checkHeadCommit() {
        if (HEAD_COMMIT == null || "".equals(HEAD_COMMIT)) {
            logAndExitVariableNotFound("HEAD_COMMIT",
                    "Please provide the current commit in the branch you want to merge.",
                    12);
        } else {
            LOGGER.info("Environment variable provided: HEAD_COMMIT");
        }
    }

    private void checkBaseCommit() {
        if (BASE_COMMIT == null || "".equals(BASE_COMMIT)) {
            logAndExitVariableNotFound("BASE_COMMIT",
                    "Please provide the commit you want to merge into.",
                    13);
        } else {
            LOGGER.info("Environment variable provided: BASE_COMMIT");
        }
    }

    private void checkLocalRepoPath() {
        if (LOCAL_REPO_PATH == null || "".equals(LOCAL_REPO_PATH)) {
            logAndExitVariableNotFound("LOCAL_REPO_PATH",
                    "Please provide the path to your local repository.",
                    14);
        } else {
            LOGGER.info("Environment variable provided: LOCAL_REPO_PATH");
        }
    }

    private void checkPrNumber() {
        if (PR_NUMBER == null || "".equals(PR_NUMBER)) {
            logAndExitVariableNotFound("PR_NUMBER",
                    "Please provide the PR number.",
                    15);
        } else {
            LOGGER.info("Environment variable provided: PR_NUMBER");
        }
    }

    private void checkUsername() {
        if (USERNAME == null || "".equals(USERNAME)) {
            logAndExitVariableNotFound("USERNAME",
                    "Please provide the username.",
                    15);
        } else {
            LOGGER.info("Environment variable provided: USERNAME");
        }
    }

    private void checkToken() {
        if (TOKEN == null || "".equals(TOKEN)) {
            logAndExitVariableNotFound("TOKEN",
                    "Please provide the user token.",
                    15);
        } else {
            LOGGER.info("Environment variable provided: TOKEN");
        }
    }

    private void logAndExitVariableNotFound(String variable, String description, int exitCode) {
        ExitBlogpostChecker.exit(LOGGER, "Environment variable not provided: " + variable + ". " + description, exitCode);
    }
}
