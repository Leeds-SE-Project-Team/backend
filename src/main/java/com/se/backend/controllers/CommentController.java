package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.CommentDTO;
import com.se.backend.projection.UserDTO;
import com.se.backend.services.CommentService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @eo.api-type http
 * @eo.groupName Comment
 * @eo.path /comments
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;

    }


    /**
     * @param user
     * @param form
     * @return ApiResponse
     * @eo.name createComment
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createComment(@RequestAttribute("user") User user, @RequestBody CommentService.CreateCommentForm form) {
        try {
            commentService.createComment(user, form);
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.success("Post comment succeed");
    }


    /**
     * @param id
     * @return ApiResponse
     * @eo.name removeComment
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping
    ApiResponse<Void> removeComment(@RequestParam(required = false) Long id) {
        try {
            commentService.deleteComment(id);
            return ApiResponse.success("Comment has been removed");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @return ApiResponse
     * @eo.name getAllComment
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<CommentDTO>> getAllComment() {
        return ApiResponse.success("Get all comments", CommentDTO.toListDTO(commentService.getAllComments()).stream().filter(c -> Objects.isNull(c.getParentId())).toList());
    }


    /**
     * @param id
     * @return ApiResponse
     * @eo.name getCommentById
     * @eo.url /by_tour_id
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/by_tour_id")
    ApiResponse<List<CommentDTO>> getCommentById(@RequestParam Long id) {
        return ApiResponse.success("Get comments by tour id", CommentDTO.toListDTO(commentService.getCommentsByTourId(id)).stream().filter(c -> Objects.isNull(c.getParentId())).toList());
    }

    /**
     * @param user
     * @param commentId
     * @return ApiResponse
     * @eo.name likeComment
     * @eo.url /like
     * @eo.method post
     * @eo.request-type formdata
     */
    @PostMapping("/like")
    ApiResponse<Void> likeComment(@RequestAttribute("user") User user, @RequestParam Long commentId) {
        try {
            commentService.likeComment(user.getId(), commentId);
            return ApiResponse.success("Comment liked successfully");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param commentId
     * @return ApiResponse
     * @eo.name cancelLikeTour
     * @eo.url /like
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping("/like")
    ApiResponse<Void> cancelLikeTour(@RequestAttribute("user") User user, @RequestParam Long commentId) {
        try {
            commentService.cancelLikeComment(user.getId(), commentId);
            return ApiResponse.success("Comment like was cancelled successfully");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param user
     * @return ApiResponse
     * @eo.name getAllLikedCommentsByUserId
     * @eo.url /liked/by-user
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/liked/by-user")
    ApiResponse<List<CommentDTO>> getAllLikedCommentsByUserId(@RequestAttribute("user") User user) {
        try {
            return ApiResponse.success("Retrieved all liked comments", commentService.getAllLikedCommentsByUserId(user.getId()));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param commentId
     * @return ApiResponse
     * @eo.name getAllUsersByLikedCommentId
     * @eo.url /likes/by-comment
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/likes/by-comment")
    ApiResponse<List<UserDTO>> getAllUsersByLikedCommentId(@RequestParam Long commentId) {
        try {
            return ApiResponse.success("Users who liked the comment", commentService.getAllUsersByLikedCommentId(commentId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


