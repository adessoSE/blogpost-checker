# blogpost-checker
Run checks to verify metadata of blog posts 📄✔️ (Designed for [Jekyll](https://jekyllrb.com) blogs).

Terminates with a non 0 status on [errors](#Error-codes) ❌.
Can be integrated with GitHub Actions to [check incoming pull requests](#Execution-via-GitHub-Action) 🚦.
Used in production for our very own [adesso devblog](github.com/adessoAG/devblog).

## Executed checks
By default, executed checks depend on `authors.yml` and the metadata of the most recent blog post.
We thus have two sets of checks that will be run.
You can add [custom checks as well](#Adding-custom-checks).

### Checking post metadata
Metadata for adesso blog posts is expected to look like this:

```
---
layout: [post, post-xml]              
title:  "Title"
date:   YYYY-MM-DD HH:MM      
modified_date: YYYY-MM-DD HH:MM
author: authorNickname
categories: [a single category]
tags: [tag 1, tag2, tag 3]
---
```

These checks are currently executed for the post metadata:
* `categories` cannot be empty
* `categories` must contain only one entry
* `categories` must be placed in brackets (`[ ]`)
* `tags` cannot be empty
* `tags` must be placed in brackets (`[ ]`)
* `author` cannnot be empty
* `author` the name must be listed in the authors.yml file
* `title` cannot be empty
* `title` has to be placed in quotes
* `layout` must equal [post, post-xml]
* `date` cannot be empty
* `date` must match the format `YYYY-MM-DD HH:mm`

### Checking authors data
Every entry in `authors.yml` is expected to look like this.
A post's metadata has to have a nickname that is listed in that file.

```yml
authorNickname:
  first_name: first name
  last_name: last name
  github_username: github username
  email: author email
  bio: "author bio"
  avatar_url: /assets/images/avatars/<author imange name>.png
  github: github link to author
```

These checks are currently executed for the `authors.yml`:
* `first_name` cannot be empty
* `last_name` cannot be empty
* `github_username` cannot be empty
* `email` cannot be empty
* `email` must match the format `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$`
* `bio` cannot be empty
* `avatar_url` cannot be empty
* `github` cannot be empty

# Usage
BlogpostChecker comes in a docker container and can either be used standalone or in a GitHub Action.
A non zero exit code will indicate a failed check.
This mechanism can be applied to check pull requests.

## Required arguments
Two arguments are required to run the application:
- `REPOSITORY_REMOTE_URL` = https://a-url-to-your-repository
- `REPOSITORY_BRANCH_NAME` = the-git-branch-to-be-checked

Updates to the codebase are pushed to [jekyll2cms/blogpost-checker](https://hub.docker.com/r/jekyll2cms/blogpost-checker).
Make sure to explicitly set the tag to `1.0.0`.
At this point, no [semantic versioning](https://semver.org/) is implemented.

# Execution via GitHub Action
Create a workflow file in `.github/workflows/run-blogpost-checker.yml`

```yml
name: run-blogpost-checker

on: [pull_request]

jobs:
  pull-and-run-blogpost-checker:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@master
        
      - name: Pull Docker image
        run: docker pull jekyll2cms/blogpost-checker:1.0.0

      - name: Run Docker image
        run: docker run --env REPOSITORY_REMOTE_URL='${{ secrets.REPOSITORY_REMOTE_URL }}' --env REPOSITORY_BRANCH_NAME='${{ github.head_ref }}' jekyll2cms/blogpost-checker:1.0.0
```

In the case of the adesso devblog, we want every pull request to be checked and thus set `REPOSITORY_BRANCH_NAME` dynamically to the current branch.
This is achieved via `${{ github.head_ref }}`.

We also use a [GitHub Secret](https://docs.github.com/en/free-pro-team@latest/actions/reference/encrypted-secrets#creating-encrypted-secrets-for-a-repository) to store `REPOSITORY_REMOTE_URL`.
You don't have to though.

# Execution via Docker
The process is very similar.
Environment arguments are passed as simple strings though.

```docker
docker run 
--env REPOSITORY_REMOTE_URL=https://a-url-to-your-repository 
--env REPOSITORY_BRANCH_NAME=the-git-branch-to-be-checked 
jekyll2cms/blogpost-checker:1.0.0
```

# Execution via Gradle
The application can be run directly from your IDE.
We recommend this approach only for development purposes.

1. Initialize the [Gradle](https://gradle.org/ ) project
2. Make sure to set the environment arguments!
3. Run `gradle bootRun`.

Follow this guide to [define environment arguments in IntelliJ](https://www.jetbrains.com/help/objc/add-environment-variables-and-program-arguments.html#add-environment-variables).

## Known issue with Gradle execution
There is a known issue with consecutive runs:

```java
UNDEFINED EXCEPTION
org.eclipse.jgit.api.errors.JGitInternalException: Destination path "repository-to-be-checked" already exists and is not an empty directory
```

Delete the `repository-to-be-checked` directory and try again if you encounter this error.

# Adding custom checks
Your custom check methods should be added inside `CheckExecutor.java`.
A post's metadata and the author value can be passed as arguments.

The method structure might look like this:

```java
 private void checkMyCustomCondition(PostMetadata metadata, String authors) {
        if (<your check condition>) {
            LOGGER.info("<your check was susccessful>");
        } else {
            ExitBlogpostChecker.exit(LOGGER, "<your check failed due to your condition not being met>", <your custom error code>);
        }
  }
```

# Error codes
Available error codes include:

| Error code | Message |
| ---------- | ------- |
| 20 | Error while cloning remote git repository |
| 21 | Error reading authors.yml |
| 23 | Error getting git branch |
| 24 | Error accessing git api |
| 25 | Error getting file content |
| 26 | Error getting authors.yml content from git |
| 300 | No category found. Exactly one category expected |
| 301 | Two or more categories found. Exactly one category expected |
| 302 | The tags are empty. One or more tags expected |
| 303 | No author found. Exactly one author expected |
| 304 | The selected author was not found in authors.yml. Make sure author exists and is spelled correctly in the blogpost |
| 305 | Blogpost title is missing. Provide a title |
| 306 | 'Layout'does not contain 'post, post-xml'. Make sure to use 'layout: [post, post-xml]' |
| 307 | Date format error. Adapt to accepted pattern YYYY-MM-DD HH:mm |
| 310 | Author first name is missing. Provide a first name |
| 311 | Author last name is missing. Provide a last name |
| 312 | Author github username is missing. Provide a github username |
| 313 | Author email is missing. Provide a email |
| 314 | Email format error. Adapt to accepted pattern ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$ |
| 315 | Author bio is missing. Provide a bio |
| 316 | Author avatar url is missing. Provide an avatar url |
| 317 | Author github is missing. Provide a github link |