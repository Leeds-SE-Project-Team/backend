// TODO: 修改语法
package com.se.backend;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.repositories.UserRepository;
import com.se.backend.services.UserService;
import com.se.backend.utils.TimeUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


@TestMethodOrder(MethodOrderer.MethodName.class)
public class UserTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("create user")
    void test1() throws AuthException {
        // Create and configure new user
        User user = new User();
        user.setNickname("test");
        user.setPassword("test");
        user.setEmail("email@se.test");
        user.setRegisterTime(TimeUtil.getCurrentTimeString());
        user.setLatestLoginTime(TimeUtil.getCurrentTimeString());

        // Save and test created user
        userService.createUser(user);
        User targetUser = this.userService.getUserById(user.getId());
        assert Objects.equals(targetUser.getId(), user.getId());
        // Log test result
        logger.info("User test pass. Id=" + user.getId());
    }

    @Test
    @DisplayName("login with correct password ")
    void test2() throws AuthException {
        // 正确登录
        userService.pwdLogin("email@se.test", "test");
    }

    @Test
    @DisplayName("login with error password")
    void test3() {
        // 密码错误
        try {
            userService.pwdLogin("email@se.test", "test2");
            assert (false);
        } catch (AuthException e) {
            assert e.getType().equals(AuthException.ErrorType.PASSWORD_NOT_MATCH);
        }
    }

    @Test
    @DisplayName("login with non-existent user")
    void test4() {
        // 没有对应用户
        try {
            userService.pwdLogin("email@se.test2", "test2");
            assert (false);
        } catch (AuthException e) {
            assert e.getType().equals(AuthException.ErrorType.USER_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("delete user")
    void test5() throws AuthException {
        userService.deleteUser(userRepository.findAll().getLast().getId());
    }
}
