/**
 * User Service Class
 */
package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Group;
import com.se.backend.models.Profit;
import com.se.backend.models.User;
import com.se.backend.repositories.GroupRepository;
import com.se.backend.repositories.ProfitRepository;
import com.se.backend.repositories.UserRepository;
import lombok.Getter;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.se.backend.exceptions.AuthException.ErrorType.PASSWORD_NOT_MATCH;
import static com.se.backend.exceptions.ResourceException.ErrorType.*;

@Service
public class UserService {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ProfitRepository profitRepository;

    @Autowired
    public UserService(UserRepository userRepository, GroupRepository groupRepository, ProfitRepository profitRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.profitRepository = profitRepository;
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

    public User updateUserType(Long id, UpdateUserTypeForm updatedInfo) throws ResourceException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));

        existingUser.setType(updatedInfo.getType());
        return userRepository.save(existingUser);
    }

    public User buyVip(User user, User.VipPackage vipPackage) throws ResourceException {
        // 获取交易时间
        LocalDateTime newExpireTime = LocalDateTime.now(ZoneId.of("UTC")).plusHours(8);

        // 新建交易记录
        Profit profit = new Profit();
        profit.setUserId(user.getId());
        profit.setAmount(vipPackage.getAmount());
        profit.setBuyTime(newExpireTime.format(formatter));
        profit.setDescription(vipPackage.getName());
        profitRepository.saveAndFlush(profit);

        // 修改用户VIP记录
        switch (vipPackage) {
            case MONTHLY -> newExpireTime = newExpireTime.plusMonths(1);
            case QUARTERLY -> newExpireTime = newExpireTime.plusMonths(3);
            case YEARLY -> newExpireTime = newExpireTime.plusYears(1);
            case FOREVER -> newExpireTime = newExpireTime.plusYears(100);
        }
        user.setVipExpireTime(newExpireTime.format(formatter));
        user.setType(User.UserType.VIP);
        return userRepository.save(user);
    }

    public User cancelVip(Long id) throws ResourceException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        existingUser.setType(User.UserType.COMMON);
        return userRepository.save(existingUser);
    }


    //TODO
    public List<Map.Entry<LocalDate, Double>> predictWeeklyRevenue() {
        List<Profit> profits = profitRepository.findAll();
        SimpleRegression regression = new SimpleRegression(true);

        // 将收入数据按周聚合
        Map<LocalDate, Double> weeklySums = profits.stream().collect(Collectors.groupingBy(profit -> LocalDateTime.parse(profit.getBuyTime(), formatter).toLocalDate().with(DayOfWeek.MONDAY), Collectors.summingDouble(Profit::getAmount)));

        // 填充回归模型数据
        LocalDate minDate = weeklySums.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.now());
        weeklySums.forEach((week, sum) -> {
            long x = ChronoUnit.WEEKS.between(minDate, week);
            regression.addData(x, sum);
        });

        // 预测过去一年和未来一年的利润
        Map<LocalDate, Double> predictions = new HashMap<>(0);
        for (int i = 1; i <= 52; i++) {
            LocalDate predictionDate = LocalDate.now().plusWeeks(i);
            double predictedValue = regression.predict(ChronoUnit.WEEKS.between(minDate, predictionDate));
            predictions.put(predictionDate, (double) Math.round(predictedValue));
        }

        predictions.putAll(weeklySums);

        var result = new ArrayList<>(predictions.entrySet().stream().toList());
        result.sort(Map.Entry.comparingByKey());
        return result;
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
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        User finalTargetUser = targetUser;
        targetUser.getTourLikes().forEach(tour -> tour.getLikedBy().remove(finalTargetUser));
        targetUser.getTourStars().forEach(tour -> tour.getStarredBy().remove(finalTargetUser));
        targetUser.getCommentLikes().forEach(comment -> comment.getLikedBy().remove(finalTargetUser));
        targetUser.getGroups().forEach(group -> group.getMembers().removeIf(user -> user.getId().equals(userId)));
        targetUser.getLeadingGroups().forEach(group -> group.getGroupCollections().clear());
        groupRepository.deleteAll(targetUser.getLeadingGroups());
        groupRepository.saveAllAndFlush(targetUser.getGroups());
        targetUser.getGroups().clear();
        targetUser = userRepository.saveAndFlush(targetUser);
        userRepository.delete(targetUser);
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

    @Getter
    public static class UpdateUserTypeForm {
        User.UserType type;
    }
    //update 表单 更新Vip字段完成Vip用户的创建
}
