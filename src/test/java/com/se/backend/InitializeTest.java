package com.se.backend;

import com.se.backend.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InitializeTest extends BackendApplicationTests {
    @Autowired
    UserService userService;

    @Test
    @Transactional
    void initializeServer() {
        logger.info("Backend server start successfully.");
    }
}


