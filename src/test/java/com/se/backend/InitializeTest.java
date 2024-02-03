package com.se.backend;

import com.se.backend.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class InitializeTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Test
    @DisplayName("initialize server")
    void test1() {
        logger.info("Backend server start successfully.");
    }
}


