package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.*;
import com.se.backend.repositories.CommentRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.se.backend.exceptions.AuthException.ErrorType.USER_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.COMMENT_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final TourRepository tourRepository;


    @Autowired
    public CommentService(CommentRepository commentRepository, TourRepository tourRepository) {
        this.commentRepository = commentRepository;
        this.tourRepository = tourRepository;
    }


    @Getter
    public static class CreateCommentForm {
        String content;
        Long parentId;
        Long tourId;
    }

    public Comment createComment(User author, CreateCommentForm form) throws ResourceException {

        Comment newComment = new Comment();
        newComment.setContent(form.content);
        newComment.setAuthor(author);
        if (Objects.nonNull(form.parentId)) {
            Comment parentComment = commentRepository.findById(form.parentId).orElseThrow(
                    () -> new ResourceException(COMMENT_NOT_FOUND)
            );
            newComment.setParent(parentComment);
        }

        newComment.setPublishTime(TimeUtil.getCurrentTimeString());

        Tour existingTour = tourRepository.findById(form.tourId).orElseThrow(
                () -> new ResourceException(TOUR_NOT_FOUND)
        );
        newComment.setTour(existingTour);

        return commentRepository.saveAndFlush(newComment);

    }

    public void deleteComment(Long commentId) throws ResourceException {

        commentRepository.findById(commentId).orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND));
        commentRepository.deleteById(commentId);

    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
