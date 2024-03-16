package com.app.Controller;


import com.app.DTO.LoginDTO;
import com.app.DTO.RegisterDTO;
import com.app.Model.User;
import com.app.Service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;

    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO){
        try{
            if(!isValidEmail(registerDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
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
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        try {
            String token = userService.login(loginDTO);
            Map<String,String> response = new HashMap<>();
            response.put("token",token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid username or password.");
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_REGEX =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
