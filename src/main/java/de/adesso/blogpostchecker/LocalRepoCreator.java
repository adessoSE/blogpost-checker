package de.adesso.blogpostchecker;

import org.eclipse.jgit.api.Git;

public abstract class LocalRepoCreator {

    private static Git localGit;

    static Git getLocalGit() {
        return localGit;
    }

    static void setLocalGit(Git local) {
        localGit = local;
    }
}
