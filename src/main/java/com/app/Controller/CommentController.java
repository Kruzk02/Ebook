package com.app.Controller;

import com.app.DTO.CommentDTO;
import com.app.Model.Comment;
import com.app.Model.User;
import com.app.Service.CommentService;
import com.app.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Autowired
    public CommentController(CommentService commentService, UserService userService, SessionRepository sessionRepository) {
        this.commentService = commentService;
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @Operation(summary = "Create an comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Create an comment", content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Comment.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public ResponseEntity<Comment> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                              description = "Comment to created",required = true,
                                              content = @Content(mediaType = "application/json",
                                              schema = @Schema(implementation = Comment.class)))
                                              @RequestBody CommentDTO commentDTO,
                                              HttpServletRequest request) {
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = session.getAttribute("username");
        User user = userService.findByUsername(username);

        commentDTO.setUser(user);

        Comment comment = commentService.save(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(comment);
    }

    @Operation(summary = "Get a comment by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found a comment", content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Comment.class))}),
        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(@Parameter(description = "Id of the comment searched") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(commentService.getById(id));
    }

    @Operation(summary = "Delete a comment by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success delete an comment", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Parameter(description = "Id of the comment deleted") @PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.APPLICATION_JSON).body("Success delete an comment by it id: " + id);
    }

}
