package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;


/**
 * @eo.api-type http
 * @eo.groupName Auth
 * @eo.path /auth
 */

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

    /**
     * @eo.name pwdLogin
     * @eo.url /pwd
     * @eo.method post
     * @eo.request-type json
     * @param req
     * @return ApiResponse
     */
    @IgnoreToken
    @PostMapping("/pwd")
    public ApiResponse<String> pwdLogin(@RequestBody ReqLoginForm req) {
        String resData;
        try {
            System.out.println(req.toString());
            User userRecord = userService.pwdLogin(req.email, req.password);
            resData = tokenService.generateTokenRecord(userRecord, "pc").getToken();
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.success("login succeed", resData);
    }


    @Getter
    public static class ReqLoginForm {
        String email;
        String password;
//        String osPlatform;
    }
}


