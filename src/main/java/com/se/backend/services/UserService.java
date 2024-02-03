/**
 * User Service Class
 */
package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.AuthException.ErrorType.PASSWORD_NOT_MATCH;
import static com.se.backend.exceptions.AuthException.ErrorType.USER_NOT_FOUND;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) throws AuthException {
        return userRepository.findById(userId).orElseThrow(
                () -> new AuthException(USER_NOT_FOUND)
        );
    }

    public User createUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(User user) throws AuthException {
        User existingUser = getUserById(user.getId());
        // Update the properties of the existing user
        existingUser.setNickname(user.getNickname());
        existingUser.setEmail(user.getEmail());
        // Update other properties as needed
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void pwdLogin(String email, String password) throws AuthException {
        User targetUser = userRepository.findByEmail(email).orElseThrow(
                () -> new AuthException(USER_NOT_FOUND)
        );
        if (!targetUser.getPassword().equals(password)){
            throw new AuthException(PASSWORD_NOT_MATCH);
        }
    }

}
