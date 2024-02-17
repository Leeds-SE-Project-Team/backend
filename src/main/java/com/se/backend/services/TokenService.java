package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.Token;
import com.se.backend.repositories.TokenRepository;

import com.se.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public String generateToken() {
        // TODO: 生成Token的逻辑
        return String.valueOf(new Date().getTime());
    }

    public Token generateTokenRecord(String email, String osPlatform) throws AuthException {
        Token tokenRecord = new Token();
        tokenRecord.setToken(generateToken());
        tokenRecord.setUser(userRepository.findByEmail(email).orElseThrow(() -> new AuthException(AuthException.ErrorType.USER_NOT_FOUND)));
        tokenRecord.setOsPlatform(osPlatform);
        return tokenRepository.saveAndFlush(tokenRecord);
    }

    public boolean validateToken(String token) {
        Optional<Token> targetToken = tokenRepository.findByToken(token);
        return targetToken.isPresent();
    }
}
