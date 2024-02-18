package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.Token;
import com.se.backend.models.User;
import com.se.backend.repositories.TokenRepository;

import com.se.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.se.backend.exceptions.AuthException.ErrorType.TOKEN_EXPIRED;

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

    public Token generateTokenRecord(User user, String osPlatform) throws AuthException {
//        Map<String,String> payload = new HashMap<>();
//        payload.put("id",user.getId().toString());
//        payload.put("email",user.getEmail());
        Token tokenRecord = new Token();
        tokenRecord.setToken(generateToken());
//        tokenRecord.setToken(JWTUtils.getToken(payload));
        tokenRecord.setUser(user);
        tokenRecord.setOsPlatform(osPlatform);

        return tokenRepository.saveAndFlush(tokenRecord);
    }

    public boolean validateToken(String token) {
        Optional<Token> targetToken = tokenRepository.findByToken(token);
        return targetToken.isPresent();
    }

    public User getUserByToken(String token) throws AuthException {
        Token tokenRecord = tokenRepository.findByToken(token).orElseThrow(() -> new AuthException(TOKEN_EXPIRED));
        return tokenRecord.getUser();
    }
}
