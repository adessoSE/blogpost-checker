# blogpost-checker
Checks the format of blogposts for jekyll blogs. An example for such a blog is the [adesso devblog](https://github.com/adessoAG/devblog). BlogpostChecker checks if certain formats for the `authors.yml` and the blogpost metadata are met. 

## executed checks
The post metadata is expected to look something like this:
```
---
layout: [post, post-xml]              
title:  "Title"            
date:   date              
modified_date:        
author: auhorname                       
categories: [a category]
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

An entry in the `authors.yml` file is expected to look something like this, where namekey has to match the author name specified in the post metadata:
```
namekey:
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

## usage
BlogpostChecker comes in as a docker container and is meant to be used in a GitHub action. It finishes with a non zero exit code if the checks fail so it can be used to evaluate a pull request. 

```
// TODO once the image is published on jekyll2cms/blogpostchecker
```

## add custom checks
According to different requirements different checks might be needed. In order to implement your own checks, you can edit the `CheckExecutor.java`. In there you can write your own check method and pass the metadata or the author or both as parameters to your method. The method itself should be structured like this:
```
if (<your check condition>) {
    LOGGER.info("<your check was susccessful>");
} else {
    ExitBlogpostChecker.exit(LOGGER, "<your check failed due to your condition not being met>", <your custom error code>);
}
```
