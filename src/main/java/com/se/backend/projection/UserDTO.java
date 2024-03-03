package com.se.backend.projection;

import com.se.backend.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    Long id;
    String nickname;
    String email;
    String avatar;
    //    String password;
    String registerTime;
    String latestLoginTime;


    public UserDTO(User user) {
        id = user.getId();
        nickname = user.getNickname();
        email = user.getEmail();
        avatar = user.getAvatar();
//        password = user.getPassword();
        registerTime = user.getRegisterTime();
        latestLoginTime = user.getLatestLoginTime();
    }

    public static List<UserDTO> toListDTO(List<User> userList) {

        return userList.stream().map(User::toDTO).toList();
    }
}
