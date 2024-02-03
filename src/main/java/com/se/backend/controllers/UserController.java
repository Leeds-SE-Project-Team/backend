/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    /**
     * 添加用户
     *
     * @param newUser 新用户信息
     * @return 创建的用户信息
     */
    @RequestMapping(method = RequestMethod.POST)
    ApiResponse<User> addUser(@RequestBody User newUser) {
        return ApiResponse.success("注册成功!", service.createUser(newUser));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 对应ID的用户信息
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) throws AuthException {
        return service.getUserById(id);
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
        return service.updateUser(updatedUser);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void removeUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
