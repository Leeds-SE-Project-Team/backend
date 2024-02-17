/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
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
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
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
     * 根据ID或者邮箱获取用户信息
     *
     * @param id,email 用户ID, 邮箱
     * @return ApiResponse<对应ID的用户信息>
     */
    @RequestMapping(method = RequestMethod.GET)
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


    /**
     * 更新用户信息
     *
     * @param id          用户ID
     * @param updatedUser 更新后的用户信息
     * @return 更新后的用户信息
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    User updateUser(@PathVariable Long id, @RequestBody User updatedUser) throws AuthException {
        return userService.updateUser(updatedUser);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void removeUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
