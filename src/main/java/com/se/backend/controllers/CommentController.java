package com.se.backend.controllers;

import com.se.backend.dto.CommentDTO;
import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Comment;
import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.services.CommentService;
import com.se.backend.services.TourService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(TourService tourService, CommentService commentService) {
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
//        List<Comment> list = objectMapper.readValue(commentService.getAllComments().toString(), new TypeReference<List<Comment>>() {
//        });
        System.err.println(commentService.getAllComments().getFirst());
        System.err.println("!!!");
        return ApiResponse.success("Get all comments", commentService.getAllComments());
    }


}


