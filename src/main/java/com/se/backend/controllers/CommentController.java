package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.CommentDTO;
import com.se.backend.services.CommentService;
import com.se.backend.services.TourService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 创建行程
     *
     * @param form 行程创建表单
     * @return ApiResponse<Void>
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
     * 删除评论信息
     *
     * @param id 评论ID
     * @return ApiResponse<Void>
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

//    @IgnoreToken
//    @GetMapping(value = "/all")
//    ApiResponse<String> getAllComment() {
//        return ApiResponse.success("Get all comments", commentService.getAllComments().toString());
//    }

    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<CommentDTO>> getAllComment() {
        // FIXME: Json process
        return ApiResponse.success("Get all comments", CommentDTO.toListDTO(commentService.getAllComments()));
    }
}


