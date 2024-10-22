package com.se.backend;


import com.se.backend.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootTest
class BackendApplicationTests{
    static final Logger logger = LoggerFactory.getLogger(BackendApplicationTests.class);

    @Autowired
    UserService userService;
}

