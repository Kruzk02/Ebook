package com.app.Controller;

import com.app.Model.User;
import com.app.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Autowired
    public UserController(UserService userService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request){
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session == null || session.getAttribute("username") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = session.getAttribute("username");
        User user = userService.findByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
