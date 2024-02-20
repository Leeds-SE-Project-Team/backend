/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.UserService;
import com.se.backend.utils.AdminToken;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    @AdminToken
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success("Get all users", userService.getAllUsers());
    }


    // Signup request form from client
    @Getter
    public static class ReqSignupForm {
        String email;
        String nickname;
        String password;
    }

    /**
     * 添加用户
     *
     * @param req 新用户信息
     * @return access token
     */
    @IgnoreToken
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    ApiResponse<Void> addUser(@RequestBody ReqSignupForm req) {
        try {
            userService.getUserByEmail(req.email);
            return ApiResponse.error("User already exist");
        } catch (AuthException e) {
            if (e.getType().equals(AuthException.ErrorType.USER_NOT_FOUND)) {
                User newUser = new User();
                newUser.setNickname(req.nickname);
                newUser.setEmail(req.email);
                newUser.setPassword(req.password);
                newUser.setRegisterTime(TimeUtil.getCurrentTimeString());
                newUser.setLatestLoginTime(TimeUtil.getCurrentTimeString());
                userService.createUser(newUser);
//                String resData = tokenService.generateToken();
                return ApiResponse.success("Signup succeed!");
            } else {
                return ApiResponse.error(e.getMessage());
            }
        }
    }

    /**
     * 根据ID或者邮箱验证对应用户是否存在
     *
     * @param id,email 用户ID, 邮箱
     * @return ApiResponse<boolean>
     */
    @IgnoreToken
    @GetMapping(value = "/exist")
    public ApiResponse<Void> isUserExist(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        try {
            if (id != null) {
                userService.getUserById(id);
                return ApiResponse.success("GET user succeed with id");
            } else if (email != null) {
                userService.getUserByEmail(email);
                return ApiResponse.success("GET user succeed with email");
            }
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }


    /**
     * 根据ID或者邮箱获取用户信息
     *
     * @param id,email 用户ID, 邮箱
     * @return ApiResponse<对应ID的用户信息>
     */
    @AdminToken
    @GetMapping
    public ApiResponse<User> getSingleUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        try {
            if (id != null) {
                return ApiResponse.success("GET user succeed with id", userService.getUserById(id));
            } else if (email != null) {
                return ApiResponse.success("GET user succeed with email", userService.getUserByEmail(email));
            }
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }
//    public ApiResponse<User> getSingleUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
//        if (Objects.equals(user.getId(), id) || Objects.equals(user.getEmail(), email)) {
//            return ApiResponse.success("GET user succeed", user);
//        }
//        return ApiResponse.error("Error when getting user");
//    }




    /**
     * 更新用户信息
     *
     * @param user        用户
     * @param updatedInfo 更新后的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping
    ApiResponse<User> updateUser(@RequestAttribute("user") User user, @RequestBody UserService.ReqUpdateForm updatedInfo) {
        try {
            return ApiResponse.success("User information updated", userService.updateUser(user.getId(), updatedInfo));
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * 删除用户信息
     *
     * @param user 用户ID
     * @return ApiResponse<Void>
     */
    @DeleteMapping
    ApiResponse<Void> removeUser(@RequestAttribute("user") User user) {
        try {
            userService.deleteUser(user.getId());
            return ApiResponse.success("User has been removed");
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
