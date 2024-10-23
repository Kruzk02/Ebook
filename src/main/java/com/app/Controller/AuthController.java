package com.app.Controller;


import com.app.DTO.LoginDTO;
import com.app.DTO.RegisterDTO;
import com.app.Model.User;
import com.app.Service.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @Operation(summary = "Register user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Success create account",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Username or email is already taken", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               description = "Register data to be created", required = true,
                                               content = @Content(mediaType = "application/json",schema = @Schema(implementation = User.class)))
                                               @RequestBody RegisterDTO registerDTO){
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

        userService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("Success create account");
    }

    @Operation(summary = "Login account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success Login", content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Map.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login data", required = true,
            content = @Content(mediaType = "application/json",schema = @Schema(implementation = User.class)))
            @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        userService.login(loginDTO);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Session session = sessionRepository.findById(request.getSession().getId());
        if (session != null) {
            session.setAttribute("username", loginDTO.getUsername());
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            sessionRepository.save(session);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Map.of("message", "Login successful", "username", loginDTO.getUsername()));
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
