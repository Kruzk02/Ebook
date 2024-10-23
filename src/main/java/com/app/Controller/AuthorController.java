package com.app.Controller;

import com.app.DTO.AuthorDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Service.EbookService;
import com.app.Service.Impl.AuthorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Get all authors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get all authors", content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Author.class))}),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthor(){
        List<Author> authors = authorService.getAllAuthor();
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @Operation(summary = "Create an author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Create an author", content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Author.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public ResponseEntity<Author> createAuthor(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                              description = "Author to created",required = true,
                                              content = @Content(mediaType = "application/json",
                                              schema = @Schema(implementation = Author.class)))
                                              @RequestBody AuthorDTO authorDTO){
        Author author = authorService.save(authorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @Operation(description = "Delete an author by it id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",description = "Success delete an author by it id", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@Parameter(description = "id of the author to deleted") @PathVariable Long id){
        authorService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
