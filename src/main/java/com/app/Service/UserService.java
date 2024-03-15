package com.app.Service;

import com.app.DTO.LoginDTO;
import com.app.DTO.RegisterDTO;
import com.app.Model.User;

public interface UserService {
    User register(RegisterDTO registerDTO);
    void login(LoginDTO loginDTO);
    User findByUsername(String username);
    User findById(Long id);
    User findByEmail(String email);
}
