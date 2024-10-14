package com.app.Controller;

import com.app.DTO.EBookDTO;
import com.app.Exceptions.AuthorNotFoundException;
import com.app.Model.Ebook;
import com.app.Model.User;
import com.app.Service.EbookService;
import com.app.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/ebook")
public class EBookController {

    private final EbookService bookService;
    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Autowired
    public EBookController(EbookService bookService, UserService userService, SessionRepository sessionRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllEBook(){
        List<Ebook> ebook = bookService.getAllEBook();
        return ResponseEntity.status(HttpStatus.OK).body(ebook);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadEBook(@ModelAttribute EBookDTO eBookDTO,
                                         HttpServletRequest request,
                                         @RequestParam("file")MultipartFile file) throws AuthorNotFoundException, InterruptedException, ExecutionException {
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session == null || session.getAttribute("username") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = session.getAttribute("username");
        User user = userService.findByUsername(username);
        eBookDTO.setUploadBy(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.asyncSave(eBookDTO,file).get());
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<?> downloadEBook(@PathVariable Long id) throws IOException{
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

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEBookById(@PathVariable Long id){
        Ebook ebook = bookService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ebook);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEBookById(@PathVariable Long id){
        bookService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
