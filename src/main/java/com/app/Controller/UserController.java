package com.app.Controller;

import com.app.JWT.JwtProvider;
import com.app.Model.User;
import com.app.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader){
        String token = extractToken(authHeader);
        if(token != null){
            String username = jwtProvider.extractUsername(token);
            User user = userService.findByUsername(username);

            Map<String,Object> entity = new HashMap<>();
            entity.put("role",user.getRoles());

            String infoToken = jwtProvider.generateToken(user.getUsername(),entity);

            Map<String,Object> response = new HashMap<>();
            response.put("Token",infoToken);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            System.out.println("WHAT");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization Header");
        }
    }

    private String extractToken(String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer")){
            return authHeader.substring(7);
        }
        return null;
    }
}
