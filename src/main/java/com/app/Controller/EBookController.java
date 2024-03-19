package com.app.Controller;

import com.app.DTO.EBookDTO;
import com.app.Model.Ebook;
import com.app.Service.Impl.EBookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/ebook")
public class EBookController {

    private final EBookServiceImpl bookService;

    @Autowired
    public EBookController(EBookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<?> getAllEBook(){
        try{
            List<Ebook> ebook = bookService.getAllEBook();
            return ResponseEntity.status(HttpStatus.OK).body(ebook);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadEBook(@ModelAttribute EBookDTO eBookDTO,
                                         @RequestParam("file")MultipartFile file){
        try{
            Ebook ebook = bookService.save(eBookDTO,file);
            return ResponseEntity.status(HttpStatus.CREATED).body(ebook);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<?> downloadEBook(@PathVariable Long id){
        try{
            Ebook ebook = bookService.getById(id);
            Path filePath = Paths.get(ebook.getPdfUrl());
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+ebook.getFileName());
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .contentLength(Files.size(filePath))
                    .body(resource);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEBookById(@PathVariable Long id){
        try{
            Ebook ebook = bookService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(ebook);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEBookById(@PathVariable Long id){
        try{
            bookService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
