package com.app.Service.Impl;

import com.app.DTO.LoginDTO;
import com.app.DTO.RegisterDTO;
import com.app.Model.Role;
import com.app.Model.User;
import com.app.Repository.RoleRepository;
import com.app.Repository.UserRepository;
import com.app.Service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testRegister() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password");
        registerDTO.setEmail("test@example.com");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role("ROLE_USER"));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User mappedUser = new User();
        when(modelMapper.map(registerDTO, User.class)).thenReturn(mappedUser);

        User registeredUser = userService.register(registerDTO);

        assertEquals(mappedUser, registeredUser);
    }

    @Test
    public void testLogin() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        userService.login(loginDTO);

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testFindByUsername() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(expectedUser);

        User foundUser = userService.findByUsername("testuser");

        assertEquals(expectedUser, foundUser);
    }

    @Test
    public void testFindById() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        User foundUser = userService.findById(1L);

        assertEquals(expectedUser, foundUser);
    }

    @Test
    public void testFindByEmail() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testuser");
        expectedUser.setEmail("testuser@gmail.com");

        when(userRepository.findByEmail("testuser@gmail.com")).thenReturn(expectedUser);

        User foundUser = userService.findByEmail("testuser@gmail.com");

        assertEquals(expectedUser, foundUser);
    }
}
