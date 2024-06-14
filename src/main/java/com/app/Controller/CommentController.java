package com.app.Controller;

import com.app.DTO.CommentDTO;
import com.app.Model.Comment;
import com.app.Model.User;
import com.app.Service.CommentService;
import com.app.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);

        commentDTO.setUser(user);

        Comment comment = commentService.save(commentDTO);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
