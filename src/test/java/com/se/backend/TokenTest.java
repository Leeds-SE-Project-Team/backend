package com.se.backend;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TokenTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    @Test
    @DisplayName("generate and validate token")
    void test1() throws AuthException {
        // Create and configure new user
        User user = new User();
        user.setNickname("test");
        user.setPassword("test");
        user.setEmail("email@se.test");
        user.setRegisterTime(LocalDateTime.now());
        user.setLatestLoginTime(LocalDateTime.now());

        // Save and test created user
        userService.createUser(user);

        // Create and configure new token
        String tokenRecord = tokenService.generateTokenRecord(user.getId(), "web").getToken();
        assert tokenService.validateToken(tokenRecord);
        userService.deleteUser(user.getId());
    }
}