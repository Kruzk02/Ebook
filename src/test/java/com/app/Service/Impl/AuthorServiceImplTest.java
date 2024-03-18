package com.app.Service.Impl;

import com.app.DTO.AuthorDTO;
import com.app.DTO.EBookDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void getAllEBook() {
        List<Author> expectedAuthor = Collections.emptyList();
        when(authorRepository.findAll()).thenReturn(expectedAuthor);

        List<Author> actualAuthor = authorService.getAllAuthor();
        assertEquals(expectedAuthor,actualAuthor);
    }

    @Test
    void getById() {
        Long id = 1L;
        Author expectedAuthor = new Author();
        when(authorRepository.findById(id)).thenReturn(Optional.of(expectedAuthor));

        Author actualAuthor = authorService.getById(id);

        assertEquals(expectedAuthor, actualAuthor);
    }

    @Test
    void save() throws IOException {
        AuthorDTO authorDTO = new AuthorDTO();
        Author expectedAuthor = new Author();

        when(modelMapper.map(authorDTO, Author.class)).thenReturn(expectedAuthor);
        when(authorRepository.save(expectedAuthor)).thenReturn(expectedAuthor);

        Author actualAuthor = authorService.save(authorDTO);

        assertEquals(expectedAuthor, actualAuthor);
    }
}