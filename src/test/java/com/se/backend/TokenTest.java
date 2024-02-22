package com.se.backend;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import com.se.backend.utils.TimeUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 允许在非静态方法上使用 @BeforeAll 和 @AfterAll
public class TokenTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    private final String userEmail = "email2@se.test";

    @BeforeAll
    @Test
    @DisplayName("create new user")
    void beforeAll() {
        User user = new User();
        user.setNickname("test");
        user.setPassword("test");
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setEmail(userEmail);
        user.setRegisterTime(TimeUtil.getCurrentTimeString());
        user.setLatestLoginTime(TimeUtil.getCurrentTimeString());
        userService.createUser(user);
    }

    @Nested
    @DisplayName("check token record function")
    class test {
        private String pcTokenRecord;

        @Test
        @DisplayName("check token written in different osPlatform")
        void test1() throws AuthException {
            User user = userService.getUserByEmail(userEmail);

            // Create and configure new token
            pcTokenRecord = tokenService.generateTokenRecord(user, "pc").getToken();
            assertTrue(tokenService.validateToken(pcTokenRecord));

            String mobileTokenRecord = tokenService.generateTokenRecord(user, "mobile").getToken();
            assertTrue(tokenService.validateToken(mobileTokenRecord));
        }

        @Test
        @DisplayName("check token override in same osPlatform")
        void test2() throws AuthException {
            User user = userService.getUserByEmail(userEmail);
            String newTokenRecord = tokenService.generateTokenRecord(user, "pc").getToken();
            assertFalse(tokenService.validateToken(pcTokenRecord));
            assertTrue(tokenService.validateToken(newTokenRecord));
        }
    }


    @AfterAll
    @Test
    @DisplayName("delete user")
    void afterAll() throws AuthException {
        userService.deleteUser(userService.getUserByEmail(userEmail).getId());
    }
}
