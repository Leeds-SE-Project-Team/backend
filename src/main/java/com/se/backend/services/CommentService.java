package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Comment;
import com.se.backend.models.Tour;
import com.se.backend.models.User;
import com.se.backend.repositories.CommentRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Comment createComment(User author, CreateCommentForm form) throws ResourceException {

        Comment newComment = new Comment();
        newComment.setContent(form.content);
        newComment.setAuthor(author);
        if (Objects.nonNull(form.parentId)) {
            Comment parentComment = commentRepository.findById(form.parentId).orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND));
            newComment.setParent(parentComment);
        }

        newComment.setPublishTime(TimeUtil.getCurrentTimeString());

        Tour existingTour = tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        newComment.setTour(existingTour);

        return commentRepository.saveAndFlush(newComment);

    }

    @Transactional
    public void deleteComment(Long commentId) throws ResourceException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND));

        // 检查是否为父评论并且有子评论
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            // 递归删除所有子评论
            for (Comment reply : new ArrayList<>(comment.getReplies())) {
                deleteComment(reply.getId()); // 递归删除每个子评论
            }
        }

        // 删除评论本身
        commentRepository.delete(comment);
    }


    public List<Comment> getAllComments() {
        return commentRepository.findAll();
//        return commentRepository.findAll().stream().map(Comment::toDTO).toList();
    }

    public List<Comment> getCommentsByTourId(Long id) {
        return commentRepository.findAllByTourId(id);
    }


    @Getter
    public static class CreateCommentForm {
        String content;
        Long parentId;
        Long tourId;
    }
}
