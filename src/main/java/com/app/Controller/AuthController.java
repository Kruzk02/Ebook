package com.app.Controller;


import com.app.DTO.LoginDTO;
import com.app.DTO.RegisterDTO;
import com.app.Model.User;
import com.app.Service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthController(UserServiceImpl userService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO){
        if(!isValidEmail(registerDTO.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
        }

        if(!isValidPassword(registerDTO.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password should have at least 1 digit , lowercase letter, uppercase letter and special character ");
        }

        User existingEmail = userService.findByEmail(registerDTO.getEmail());
        User existingUsername = userService.findByUsername(registerDTO.getUsername());

        if(existingEmail != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken.");
        }
        if(existingUsername != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
        }

        User user = userService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            userService.login(loginDTO);
            Session session = sessionRepository.findById(request.getSession().getId());
            session.setAttribute("username", loginDTO.getUsername());
            sessionRepository.save(session);
            return ResponseEntity.ok().body(session.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_REGEX =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password){
        String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
