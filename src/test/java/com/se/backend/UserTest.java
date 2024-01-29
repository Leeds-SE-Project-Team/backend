package com.se.backend;

import com.se.backend.models.User;
import com.se.backend.repositories.UserRepository;
import com.se.backend.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Test
    @Transactional
    void contextLoads() {
        // Create and configure new user
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setEmail("email@se.test");
        user.setRegisterTime(LocalDateTime.now());
        user.setLatestLoginTime(LocalDateTime.now());

        // Save and test created user
        userService.createUser(user);
        User targetUser = this.userService.getUserById(user.getId());

        // Log test result
        logger.info("user={}", targetUser);
    }
}
