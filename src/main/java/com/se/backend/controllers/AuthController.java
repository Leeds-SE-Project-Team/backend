package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @RequestMapping("/login/pwd")
    public ApiResponse<String> pwdLogin(String email, String password){
        String resData;
        try {
            userService.pwdLogin(email, password);
            resData = tokenService.generateToken();
        } catch (AuthException e) {
            return ApiResponse.success(e.getMessage());
        }
        return ApiResponse.success("登录成功", resData);
    }
}


