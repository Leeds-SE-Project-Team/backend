/**
 * User Service Class
 */
package com.se.backend.services;

import com.se.backend.models.User;
import com.se.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)
        );
    }

    public User createUser(User user) {
        // You can add additional logic/validation before saving the user
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        // Update the properties of the existing user
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        // Update other properties as needed
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
