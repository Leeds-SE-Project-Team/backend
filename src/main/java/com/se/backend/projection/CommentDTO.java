package com.se.backend.projection;

import com.se.backend.models.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class CommentDTO {
    Long id;
    Long tourId;
    UserDTO author;
    String content;
    String publishTime;
    List<CommentDTO> replies;
    Long parentId; // avoid circular loop serialization error

    // constructor
    public CommentDTO(Comment comment) {
        id = comment.getId();
        tourId = comment.getTour().getId();
        author = comment.getAuthor().toDTO();
        content = comment.getContent();
        publishTime = comment.getPublishTime();
        replies = toListDTO(comment.getReplies());
        parentId = Objects.isNull(comment.getParent()) ? null : comment.getParent().getId();
    }

    // convert List<Comment> to List<CommentDTO>
    public static List<CommentDTO> toListDTO(List<Comment> commentList) {
        if (Objects.isNull(commentList)) {
            return new ArrayList<>(0);
        }
        return commentList.stream().map(Comment::toDTO).toList();
    }
}
