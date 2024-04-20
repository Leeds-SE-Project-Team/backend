/**
 * User Service Class
 */
package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Group;
import com.se.backend.models.User;
import com.se.backend.repositories.GroupRepository;
import com.se.backend.repositories.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.se.backend.exceptions.AuthException.ErrorType.PASSWORD_NOT_MATCH;
import static com.se.backend.exceptions.AuthException.ErrorType.USER_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.GROUP_NOT_FOUND;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) throws AuthException {
        return userRepository.findById(userId).orElseThrow(() -> new AuthException(USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) throws AuthException {
        return userRepository.findByEmail(email).orElseThrow(() -> new AuthException(USER_NOT_FOUND));
    }


    public User createUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(Long id, ReqUpdateForm updatedInfo) throws AuthException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new AuthException(USER_NOT_FOUND));

        // 验证旧密码，仅当用户想要更改密码时
        if (Objects.nonNull(updatedInfo.getOldPassword()) && !updatedInfo.getOldPassword().isEmpty()) {
            if (!existingUser.getPassword().equals(updatedInfo.getOldPassword())) {
                throw new AuthException(PASSWORD_NOT_MATCH);
            }
            // 更新密码
            existingUser.setPassword(updatedInfo.getNewPassword());
        }

        // 更新其他属性
        existingUser.setNickname(updatedInfo.getNickname());
        existingUser.setAvatar(updatedInfo.getAvatar());
        existingUser.setEmail(updatedInfo.getEmail());
        existingUser.setType(updatedInfo.getType());

        return userRepository.save(existingUser);
    }

    public User addUserToGroup(Long GroupId, Long UserId) throws AuthException, ResourceException {
        User existingUser = userRepository.findById(UserId).orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        // Update the properties of the existing user
        Group existingGroup = groupRepository.findById(GroupId).orElseThrow(() -> new ResourceException(GROUP_NOT_FOUND));
        existingGroup.getMembers().add(existingUser);
        existingUser.getGroups().add(existingGroup);
        groupRepository.save(existingGroup);
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) throws AuthException {
        userRepository.findById(userId).orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        userRepository.deleteById(userId);
    }

    public User pwdLogin(String email, String password) throws AuthException {
        User targetUser = userRepository.findByEmail(email).orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        if (!targetUser.getPassword().equals(password)) {
            throw new AuthException(PASSWORD_NOT_MATCH);
        }
        return targetUser;
    }


    // Update request form from client
    @Getter
    public static class ReqUpdateForm {
        String email;
        String avatar;
        String nickname;
        String oldPassword;
        String newPassword;
        User.UserType type;
    }
    //update 表单 更新Vip字段完成Vip用户的创建
}
