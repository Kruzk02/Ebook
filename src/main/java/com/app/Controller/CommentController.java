package com.app.Controller;

import com.app.DTO.CommentDTO;
import com.app.JWT.JwtProvider;
import com.app.Model.Comment;
import com.app.Model.User;
import com.app.Service.CommentService;
import com.app.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public CommentController(CommentService commentService, UserService userService, JwtProvider jwtProvider) {
        this.commentService = commentService;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CommentDTO commentDTO,@RequestHeader("Authorization") String authHeader){
        String token = extractToken(authHeader);

        if(token != null){
            String username = jwtProvider.extractUsername(token);
            User user = userService.findByUsername(username);

            commentDTO.setUser(user);
            Comment comment = commentService.save(commentDTO);
            return ResponseEntity.status(HttpStatus.OK).body(comment);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        commentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String extractToken(String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer")){
            return authHeader.substring(7);
        }
        return null;
    }
}
