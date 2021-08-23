package de.adesso.blogpostchecker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMetadata {
    private String layout;
    private String title;
    private String date;
    private String author;
    private String categories;
    private String tags;
}
