package de.adesso.blogpostchecker;

import java.util.Objects;

public class PostMetadata {
    private String layout;
    private String title;
    private String date;
    private String author;
    private String categories;
    private String tags;

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostMetadata metadata = (PostMetadata) o;
        return layout.equals(metadata.layout) &&
                title.equals(metadata.title) &&
                date.equals(metadata.date) &&
                author.equals(metadata.author) &&
                categories.equals(metadata.categories) &&
                tags.equals(metadata.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layout, title, date, author, categories, tags);
    }
}
