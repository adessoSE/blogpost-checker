package de.adesso.blogpostchecker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    private String authorNickname;
    private String firstName;
    private String lastName;
    private String githubUsername;
    private String email;
    private String bio;
    private String avatarUrl;
    private String github;
}
