package com.app.Controller;

import com.app.DTO.EBookDTO;
import com.app.Exceptions.AuthorNotFoundException;
import com.app.Model.Ebook;
import com.app.Model.User;
import com.app.Service.EbookService;
import com.app.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.concurrent.CompletableFuture;
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

    @Operation(summary = "Get all ebooks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all ebooks", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Ebook.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
        }
    )
    @GetMapping
    public ResponseEntity<List<Ebook>> getAllEBook(){
        List<Ebook> ebook = bookService.getAllEBook();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(ebook);
    }

    @Operation(summary = "Save an new ebook")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Save an Ebook", content = { @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = Ebook.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "415", description = "File type is not pdf", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "413", description = "File is large than 10mb", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
        }
    )
    @PostMapping("/upload")
    public ResponseEntity<CompletableFuture<Ebook>> uploadEBook(@RequestBody(description = "Ebook to created", required = true,
                                         content = @Content(mediaType = "multipart/form-data",
                                         schema = @Schema(implementation = Ebook.class)))
                                         @ModelAttribute EBookDTO eBookDTO,
                                                                HttpServletRequest request,
                                                                @RequestParam("file")MultipartFile file) throws AuthorNotFoundException, InterruptedException, ExecutionException {
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session == null || session.getAttribute("username") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = session.getAttribute("username");
        User user = userService.findByUsername(username);
        eBookDTO.setUploadBy(user);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.MULTIPART_FORM_DATA).body(bookService.asyncSave(eBookDTO,file));
    }

    @Operation(summary = "Download eBook", description = "Downloads the eBook specified by the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the eBook", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InputStreamResource.class))}),
            @ApiResponse(responseCode = "404", description = "eBook not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadEBook(
            @Parameter(description = "ID of the eBook to be downloaded", required = true) @PathVariable Long id) throws IOException {
        Ebook ebook = bookService.getById(id);
        Path filePath = Paths.get(ebook.getPdfUrl());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + ebook.getFileName());
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .contentLength(Files.size(filePath))
                .body(resource);
    }

    @Operation(summary = "Get a ebook by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the ebook",content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Ebook.class))}),
        @ApiResponse(responseCode = "404", description = "Ebook not found",content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error",content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ebook> getEBookById(@Parameter(description = "id of ebook to be searched") @PathVariable Long id){
        Ebook ebook = bookService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(ebook);
    }

    @Operation(summary = "Delete an ebook by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success delete an ebook", content = { @Content(mediaType = "application/json",schema = @Schema(implementation = String.class))}),
        @ApiResponse(responseCode = "404", description = "Ebook not found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEBookById(@Parameter(description = "id of ebook to be deleted") @PathVariable Long id){
        bookService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.APPLICATION_JSON).body("Success delete an ebook with a id: " + id);
    }
}
