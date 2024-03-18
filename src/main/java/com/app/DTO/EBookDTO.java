package com.app.DTO;

import com.app.Model.Author;
import com.app.Model.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EBookDTO {

    private String title;
    private String description;
    private Set<Genre> genres;
    private Set<Author> authors;

}
