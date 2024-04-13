package com.app.Service;


import com.app.DTO.CommentDTO;
import com.app.Model.Comment;

import java.io.IOException;

public interface CommentService {
    Comment getById(Long id);
    Comment save(CommentDTO commentDTO);
    void deleteById(Long id);
}
