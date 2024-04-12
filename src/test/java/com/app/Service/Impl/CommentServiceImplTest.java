package com.app.Service.Impl;

import com.app.DTO.CommentDTO;
import com.app.Exceptions.CommentNotFoundException;
import com.app.Model.*;
import com.app.Repository.CommentRepository;
import com.app.Repository.EbookRepository;
import com.app.Repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository repository;

    @Mock
    private EbookRepository ebookRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Ebook ebook;
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author(1L,"name",null);

        user = User.builder()
                .id(1L)
                .username("phuc")
                .email("phuc@gmail.com")
                .password("phuc")
                .roles(Arrays.asList(roleRepository.findByName("ROLE_USER")))
                .build();

        Set<Genre> genres = Collections.singleton(Genre.COMEDY);
        Set<Author> authors = Collections.singleton(author);
        ebook = Ebook.builder()
                .id(1L)
                .title("title")
                .description("description")
                .pdfUrl("")
                .fileName("")
                .genres(genres)
                .authors(authors)
                .build();
    }

    @Test
    void getById() {
        Comment expectedComment = new Comment(1L,"content",user,ebook);
        when(repository.findById(expectedComment.getId())).thenReturn(Optional.of(expectedComment));

        Comment actualComment = commentService.getById(expectedComment.getId());

        assertEquals(expectedComment,actualComment);
    }

    @Test
    void save() {
        CommentDTO commentDTO = new CommentDTO("content", user, 1L);
        Comment expectedComment = new Comment(1L, "content", user, ebook);

        when(ebookRepository.findById(commentDTO.getEbookId())).thenReturn(Optional.of(ebook));
        when(modelMapper.map(commentDTO, Comment.class)).thenReturn(expectedComment);
        when(repository.save(expectedComment)).thenReturn(expectedComment);

        Comment actualComment = commentService.save(commentDTO);

        assertEquals(expectedComment, actualComment);
    }


    @Test
    void deleteById() {
        Comment comment = new Comment(1L,"content",user,ebook);
        when(repository.existsById(comment.getId())).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteById(comment.getId()));
    }
}
