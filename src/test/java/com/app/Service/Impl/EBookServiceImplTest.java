package com.app.Service.Impl;

import com.app.DTO.EBookDTO;
import com.app.Model.Ebook;
import com.app.Repository.EbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EBookServiceImplTest {

    @Mock
    private EbookRepository ebookRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EBookServiceImpl eBookService;

    @Test
    void getAllEBook() {
        List<Ebook> expectedEbooks = Collections.emptyList();
        when(ebookRepository.findAll()).thenReturn(expectedEbooks);

        List<Ebook> actualEbooks = eBookService.getAllEBook();

        assertEquals(expectedEbooks, actualEbooks);
    }

    @Test
    void getById() {
        Long id = 1L;
        Ebook expectedEbook = new Ebook();
        when(ebookRepository.findById(id)).thenReturn(Optional.of(expectedEbook));

        Ebook actualEbook = eBookService.getById(id);

        assertEquals(expectedEbook, actualEbook);
    }

    @Test
    void getByAuthor() {
        // Implement test if needed
    }

    @Test
    void save() throws IOException {
        EBookDTO eBookDTO = new EBookDTO();
        MultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);
        Ebook expectedEbook = new Ebook();

        when(modelMapper.map(eBookDTO, Ebook.class)).thenReturn(expectedEbook);
        when(ebookRepository.save(expectedEbook)).thenReturn(expectedEbook);

        Ebook actualEbook = eBookService.save(eBookDTO, multipartFile);

        assertEquals(expectedEbook, actualEbook);
    }
}
