package com.se.backend.services;

import com.se.backend.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentLikeService {

    private final CommentRepository commentLikeRepository;

    @Autowired
    public CommentLikeService(CommentRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }
}
