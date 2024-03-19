package com.app.DTO;

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
    private Set<String> genres;
    private Set<String> authors;

}
