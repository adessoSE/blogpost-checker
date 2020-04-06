package de.adesso.blogpostchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitializationService {

	private GitRepoCloner repoCloner;
	private ConfigService configService;
	private CheckExecutor checkExecutor;
	private FileAnalyzer fileAnalyzer;

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
	 * Step 0: Check config
	 * Step 1: Clone repo
 	 * Step 2: Transform repo using jekyll
	 * Step 3: Execute checks
	 */
	@PostConstruct
	public void init() {
		try {
			// Step 1: Check config
			configService.checkConfiguration();

			// Step 2: Clone repo
			repoCloner.cloneRemoteRepo();
			PostMetaData header = fileAnalyzer.getMetaData();
			String authors = fileAnalyzer.getAuthors();

			// Step 3: Execute checks
			checkExecutor.executeChecks(header, authors);
		} catch(Exception e) {
			LOGGER.error("UNDEFINED EXCEPTION", e);
			LOGGER.error("Exiting BlogpostChecker.");
			System.exit(1);
		}

		LOGGER.info("Execution of BlogpostChecker successful.");
		LOGGER.info("Stopping BlogpostChecker.");
		System.exit(0);
	}
}
