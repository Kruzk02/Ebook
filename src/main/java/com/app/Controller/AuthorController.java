package com.app.Controller;

import com.app.DTO.AuthorDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Service.EbookService;
import com.app.Service.Impl.AuthorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final EbookService eBookService;
    private final AuthorServiceImpl authorService;

    @Autowired
    public AuthorController(EbookService eBookService, AuthorServiceImpl authorService) {
        this.eBookService = eBookService;
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthor(){
        List<Author> authors = authorService.getAllAuthor();
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createAuthor(@RequestBody AuthorDTO authorDTO){
        Author author = authorService.save(authorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        authorService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{authorName}")
    public ResponseEntity<?> getEBookByAuthor(@PathVariable String authorName){
        Author author = authorService.getByName(authorName);
        if(author == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Ebook ebook = eBookService.getByAuthor(author);
        if(ebook == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(ebook);
    }


}
