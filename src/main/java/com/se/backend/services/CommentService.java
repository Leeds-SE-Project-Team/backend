package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Comment;
import com.se.backend.models.Tour;
import com.se.backend.models.User;
import com.se.backend.projection.CommentDTO;
import com.se.backend.projection.UserDTO;
import com.se.backend.repositories.CommentRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.se.backend.exceptions.ResourceException.ErrorType.*;

@Service
public class CommentService {

    private final CommentRepository commentRepository;


    private final TourRepository tourRepository;

    private final UserRepository userRepository;


    @Autowired
    public CommentService(CommentRepository commentRepository, TourRepository tourRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }
    public Comment getCommentById(Long commentId) throws ResourceException{
        return commentRepository.findById(commentId).orElseThrow(()->new ResourceException(COMMENT_NOT_FOUND));
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
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND));
//
//        // 检查是否为父评论并且有子评论
//        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
//            // 递归删除所有子评论
//            for (Comment reply : new ArrayList<>(comment.getReplies())) {
//                deleteComment(reply.getId()); // 递归删除每个子评论
//            }
//        }
        // 删除评论本身
//        commentRepository.deleteById(commentId);
        commentRepository.delete(commentRepository.findById(commentId).orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND)));
    }


    public List<Comment> getAllComments() {
        return commentRepository.findAll();
//        return commentRepository.findAll().stream().map(Comment::toDTO).toList();
    }

    public List<Comment> getCommentsByTourId(Long id) {
        return commentRepository.findAllByTourId(id);
    }
    @Transactional
    public Comment likeComment(User user, Long commentId) throws ResourceException {
        Set<Comment> commentLikes = user.getCommentLikes();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceException(COMMENT_NOT_FOUND));

//        if (tourLiked.contains(tour)) {
//            throw new ResourceException(TOUR_LIKE_EXISTS);
//        }
        // 检查点赞是否已存在
        for (Comment c : commentLikes) {
            if (c.getId().equals(commentId)) {
                //  已经存在
                throw new ResourceException(COMMENT_LIKE_EXISTS);
            }
        }
//        tourLiked.add(tour);
        comment.getLikedBy().add(user);
        return commentRepository.saveAndFlush(comment);
    }

    @Transactional
    public Comment cancelLikeComment(User user, Long commentId) throws ResourceException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceException(COMMENT_NOT_FOUND));
        comment.getLikedBy().removeIf(u->u.getId().equals(user.getId()));
        return commentRepository.saveAndFlush(comment);
    }

//    public List<CommentDTO> getAllLikedCommentsByUserId(Long userId) throws ResourceException {
//        if (!commentLikeRepository.existsById(userId)) {
//            throw new ResourceException(USER_NOT_FOUND);
//        }
//        List<CommentLike> likes = commentLikeRepository.findAllByUserId(userId);
//        return CommentDTO.toListDTO(likes.stream().map(CommentLike::getComment).collect(Collectors.toList()));
//    }
//
//    public List<UserDTO> getAllUsersByLikedCommentId(Long commentId) throws ResourceException {
//        if (!commentLikeRepository.existsById(commentId)) {
//            throw new ResourceException(COMMENT_NOT_FOUND);
//        }
//        List<CommentLike> likes = commentLikeRepository.findAllByCommentId(commentId);
//        return UserDTO.toListDTO(likes.stream().map(CommentLike::getUser).distinct().collect(Collectors.toList()));
//    }

    @Getter
    public static class CreateCommentForm {
        String content;
        Long parentId;
        Long tourId;
    }
}
