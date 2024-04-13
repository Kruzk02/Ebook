package com.app.DTO;

import com.app.Model.Ebook;
import com.app.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDTO {

    private String content;
    private User user;
    private Long ebookId;
}
