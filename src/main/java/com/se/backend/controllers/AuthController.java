package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    // Login request form from client
    @Getter
    public static class ReqLoginForm {
        String email;
        String password;
//        String osPlatform;
    }

    @PostMapping("/pwd")
    public ApiResponse<String> pwdLogin(@RequestBody ReqLoginForm req) {
        String resData;
        try {
            System.out.println(req.toString());
            userService.pwdLogin(req.email, req.password);
            resData = tokenService.generateToken();
            tokenService.generateTokenRecord(req.email, "pc");
//            tokenService.generateTokenRecord(req.email, req.osPlatform);
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.success("登录成功", resData);
    }
}


