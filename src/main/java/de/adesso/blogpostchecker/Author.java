package de.adesso.blogpostchecker;

import java.util.Objects;

public class Author {

    private String authorNickname;
    private String firstName;
    private String lastName;
    private String githubUsername;
    private String email;
    private String bio;
    private String avatarUrl;
    private String github;

    public Author() {
    }

    public Author(String authorNickname, String firstName, String lastName, String githubUsername, String email, String bio, String avatarUrl, String github) {
        this.authorNickname = authorNickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.githubUsername = githubUsername;
        this.email = email;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.github = github;
    }

    @Override
    public String toString() {
        return "Author{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", githubUsername='" + githubUsername + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", github='" + github + '\'' +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return authorNickname.equals(author.authorNickname) &&
                firstName.equals(author.firstName) &&
                lastName.equals(author.lastName) &&
                githubUsername.equals(author.githubUsername) &&
                email.equals(author.email) &&
                bio.equals(author.bio) &&
                avatarUrl.equals(author.avatarUrl) &&
                github.equals(author.github);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorNickname, firstName, lastName, githubUsername, email, bio, avatarUrl, github);
    }
}
