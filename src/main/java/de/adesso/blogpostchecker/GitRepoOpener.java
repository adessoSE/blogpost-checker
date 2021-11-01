package de.adesso.blogpostchecker;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitRepoOpener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoOpener.class);

    private final ConfigService configService;

    @Autowired
    public GitRepoOpener(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Opens the repository (defined in environment variables:
     * repository.remote.url) from the local file system (repository.local.path)
     */
    public void setRepo() {
        try {
            LOGGER.info("Start opening repository...");
            LocalRepoCreator.setLocalGit(Git.open(new File(configService.getLOCAL_REPO_PATH())));
            LOGGER.info("Repository opened successfully.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            ExitBlogpostChecker.exit(LOGGER, "Error while opening git repository: " + configService.getLOCAL_REPO_PATH(), 27);
        }
    }
}
