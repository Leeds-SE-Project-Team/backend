package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.CommentDTO;
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
     * @eo.name createComment
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     * @param user
     * @param form
     * @return ApiResponse
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
     * @eo.name removeComment
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
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
     * @eo.name getAllComment
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<CommentDTO>> getAllComment() {
        return ApiResponse.success("Get all comments", CommentDTO.toListDTO(commentService.getAllComments()).stream().filter(c -> Objects.isNull(c.getParentId())).toList());
    }


    /**
     * @eo.name getCommentById
     * @eo.url /by_tour_id
     * @eo.method get
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/by_tour_id")
    ApiResponse<List<CommentDTO>> getCommentById(@RequestParam Long id) {
        return ApiResponse.success("Get comments by tour id", CommentDTO.toListDTO(commentService.getCommentsByTourId(id)).stream().filter(c -> Objects.isNull(c.getParentId())).toList());
    }
}


