package com.app.Service.Impl;

import com.app.DTO.CommentDTO;
import com.app.Exceptions.CommentNotFoundException;
import com.app.Exceptions.EBookNotFoundException;
import com.app.Model.Comment;
import com.app.Model.Ebook;
import com.app.Repository.CommentRepository;
import com.app.Repository.EbookRepository;
import com.app.Service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EbookRepository ebookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, EbookRepository ebookRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.ebookRepository = ebookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("Comment Not Found"));
    }

    @Override
    public Comment save(CommentDTO commentDTO) {
        Ebook ebook = ebookRepository.findById(commentDTO.getEbookId()).orElseThrow(() -> new EBookNotFoundException("Ebook Not Found"));

        Comment comment = modelMapper.map(commentDTO,Comment.class);
        comment.setEbook(ebook);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(Long id) {
        if(commentRepository.existsById(id)){
            commentRepository.deleteById(id);
        }else {
            throw new CommentNotFoundException("Comment Not Found");
        }
    }
}
