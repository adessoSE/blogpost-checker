package de.adesso.blogpostchecker;

import org.slf4j.Logger;

public class ExitBlogpostChecker {
    public static void exit(Logger LOGGER, String message, int exitCode) {
        LOGGER.error(message);
        LOGGER.error("Exiting BlogpostChecker.");
        System.exit(exitCode);
    }
}
