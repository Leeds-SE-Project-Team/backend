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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.se.backend.exceptions.AuthException.ErrorType.PASSWORD_NOT_MATCH;
import static com.se.backend.exceptions.ResourceException.ErrorType.*;

@Service
public class UserService {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
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

    public User getUserById(Long userId) throws ResourceException {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) throws ResourceException {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
    }


    public User createUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(Long id, ReqUpdateForm updatedInfo) throws ResourceException, AuthException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));

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
        existingUser.setGender(updatedInfo.getGender());
        existingUser.setAge(updatedInfo.getAge());
        existingUser.setHeight(updatedInfo.getHeight());
        existingUser.setWeight(updatedInfo.getWeight());
        existingUser.setLocation(updatedInfo.getLocation());
        existingUser.setSignature(updatedInfo.getSignature());

        return userRepository.save(existingUser);
    }

    public User updateUserType(Long id, ReqUpdateForm updatedInfo) throws ResourceException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));

        existingUser.setType(updatedInfo.getType());
        return userRepository.save(existingUser);
    }

    public User updateVipExpireTime(Long id) throws ResourceException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        // 根据当前时间往后推迟一个月
        LocalDateTime newExpireTime = LocalDateTime.now(ZoneId.of("UTC")).plusMonths(1);
        existingUser.setVipExpireTime(newExpireTime.format(formatter));
        return userRepository.save(existingUser);
    }

    public void addUserToGroup(Long userId, Long groupId) throws AuthException, ResourceException {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        // Update the properties of the existing user
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(() -> new ResourceException(GROUP_NOT_FOUND));

        // Prevent the leader from adding themselves again to the group they lead
        if (existingGroup.getLeader().getId().equals(userId)) {
            throw new ResourceException(LEADER_JOIN_SELF);
        }
        existingGroup.getMembers().add(existingUser);
        existingUser.getGroups().add(existingGroup);
        groupRepository.saveAndFlush(existingGroup);
        userRepository.saveAndFlush(existingUser);
    }

    public void deleteUser(Long userId) throws AuthException, ResourceException {
        userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        userRepository.deleteById(userId);
    }

    public User pwdLogin(String email, String password) throws AuthException, ResourceException {
        User targetUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
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


        String gender;
        Integer age;
        Double height;
        Double weight;
        String location; // "XX省 XX市"
        String signature; // 个性签名
    }
    //update 表单 更新Vip字段完成Vip用户的创建
}
