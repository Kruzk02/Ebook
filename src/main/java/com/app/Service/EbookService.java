package com.app.Service;

import com.app.DTO.EBookDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public interface EbookService {
    Future<Ebook> execute(EBookDTO eBookDTO,MultipartFile file) throws InterruptedException, IOException;
    List<Ebook> getAllEBook();
    Ebook getById(Long id);
    Ebook getByAuthor(Author author);
    Ebook save(EBookDTO eBookDTO, MultipartFile multipartFile) throws IOException;
    void deleteById(Long id);
}
