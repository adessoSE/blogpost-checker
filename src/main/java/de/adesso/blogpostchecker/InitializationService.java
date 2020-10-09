package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;

@Service
@Profile("!test")
public class InitializationService {

	private final GitRepoCloner repoCloner;
	private final ConfigService configService;
	private final CheckExecutor checkExecutor;
	private final FileAnalyzer fileAnalyzer;

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	@Autowired
	public InitializationService(GitRepoCloner gitRepoCloner, ConfigService configService, CheckExecutor checkExecutor, FileAnalyzer fileAnalyzer){
		this.repoCloner = gitRepoCloner;
		this.configService = configService;
		this.checkExecutor = checkExecutor;
		this.fileAnalyzer = fileAnalyzer;
	}

	/**
	 * Init the BlogpostChecker process.
	 * Step 1: Check config
	 * Step 2: Clone repo
	 * Step 3: Execute checks
	 */
	@PostConstruct
	public void init() {
		try {
			// Step 1: Check config
			configService.checkConfiguration();

			// Step 2: Clone repo
			repoCloner.cloneRemoteRepo();
			PostMetadata header = fileAnalyzer.getMetadata();
			Author author = fileAnalyzer.getAuthor();

			// Step 3: Execute checks
			checkExecutor.executeChecks(header, author);
		} catch(Exception e) {
			ExitBlogpostChecker.exit(LOGGER, MessageFormat.format("UNDEFINED EXCEPTION {0}", e.getMessage()), 1);
		}

		ExitBlogpostChecker.exitInfo(LOGGER, "Execution of BlogpostChecker successful.", 0);
	}
}
