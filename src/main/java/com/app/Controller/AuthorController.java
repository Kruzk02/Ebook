package com.app.Controller;

import com.app.DTO.AuthorDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Service.Impl.AuthorServiceImpl;
import com.app.Service.Impl.EBookServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final EBookServiceImpl eBookService;
    private final AuthorServiceImpl authorService;

    public AuthorController(EBookServiceImpl eBookService, AuthorServiceImpl authorService) {
        this.eBookService = eBookService;
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthor(){
        try{
            List<Author> authors = authorService.getAllAuthor();
            return ResponseEntity.status(HttpStatus.OK).body(authors);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAuthor(@RequestBody AuthorDTO authorDTO){
        try {
            Author author = authorService.save(authorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(author);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            authorService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{authorName}")
    public ResponseEntity<?> getEBookByAuthor(@PathVariable String authorName){
        try{
            Author author = authorService.getByName(authorName);
            if(author == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            Ebook ebook = eBookService.getByAuthor(author);
            if(ebook == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.status(HttpStatus.OK).body(ebook);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
