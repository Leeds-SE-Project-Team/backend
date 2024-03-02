package com.se.backend.dto;

import com.se.backend.models.Comment;
import com.se.backend.models.User;

import java.util.List;
import java.util.Objects;

public class CommentDTO {
    Long id;
    Long tourId;
    User author;
    String content;
    String publishTime;
    List<Comment> replies;
    Long parentId;

    public CommentDTO() {
    }

    public CommentDTO(Comment comment) {
        id = comment.getId();
        tourId = comment.getTour().getId();
        author = comment.getAuthor();
        content = comment.getContent();
        publishTime = comment.getPublishTime();
        replies = comment.getReplies();
        parentId = Objects.isNull(comment.getParent()) ? null : comment.getParent().getId();
    }
}
