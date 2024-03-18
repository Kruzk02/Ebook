package com.app.Service;

import com.app.DTO.AuthorDTO;
import com.app.Model.Author;

import java.util.List;

public interface AuthorService {

    List<Author> getAllAuthor();
    Author getById(Long id);
    Author save(AuthorDTO authorDTO);
    void deleteById(Long id);
}
