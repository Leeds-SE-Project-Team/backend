package com.se.backend.projection;

import com.se.backend.models.Tour;
import com.se.backend.models.TourStar;
import com.se.backend.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {
    Long id;
    String nickname;
    String email;
    String avatar;
    int type;
    //    String password;
    String registerTime;
    String latestLoginTime;
    List<Long> tourLikes; // IDs of tours this user likes
    List<Long> tourStars; // IDs of tours this user has starred

    // Extra Information
    String gender;
    Integer age;
    Double height;
    Double weight;
    String location; // "XX省 XX市"
    String signature; // 个性签名


    public UserDTO(User user) {
        id = user.getId();
        nickname = user.getNickname();
        email = user.getEmail();
        avatar = user.getAvatar();
        type = user.getType().ordinal();
        registerTime = user.getRegisterTime();
        latestLoginTime = user.getLatestLoginTime();
        tourLikes = user.getTourLikes().stream().map(Tour::getId).toList();
        tourStars = user.getTourStars().stream().map(TourStar::getTour).map(Tour::getId).collect(Collectors.toList());

        // Extra Information
        gender = user.getGender();
        age = user.getAge();
        height = user.getHeight();
        weight = user.getWeight();
        location = user.getLocation();
        signature = user.getSignature();
    }

    public static List<UserDTO> toListDTO(List<User> userList) {
        if (Objects.isNull(userList)) {
            return new ArrayList<>(0);
        }
        return userList.stream().map(User::toDTO).toList();
    }
}
