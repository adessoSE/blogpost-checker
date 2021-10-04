package de.adesso.blogpostchecker;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Profile("local")
public class GitRepoCloner implements GitRepoGetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoCloner.class);

    private final ConfigService configService;

    @Autowired
    public GitRepoCloner(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Clones the remote repository (defined in environment variables:
     * repository.remote.url) to the local file system (repository.local.path)
     */
    public void setRepo() {
        try {
            LOGGER.info("Start cloning repository...");
            LocalRepoCreater.setLocalGit(Git.cloneRepository()
                    .setURI(configService.getREPOSITORY_REMOTE_URL())
                    .setDirectory(new File(configService.getLOCAL_REPO_PATH()))
                    .call());
            LOGGER.info("Repository cloned successfully.");
        } catch (GitAPIException e) {
            LOGGER.error(e.getMessage(), e);
            ExitBlogpostChecker.exit(LOGGER, "Error while cloning remote git repository: " + configService.getREPOSITORY_REMOTE_URL(), 20);
        }
    }
}
