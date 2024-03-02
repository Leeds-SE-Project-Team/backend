package com.se.backend.models;

import com.se.backend.config.GlobalConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//spring data JPA

@Entity
@Table(name = "user")
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 50, unique = true)
    private String email;

    @Column(nullable = false)
    private String avatar;
    public static final String DEFAULT_AVATAR = GlobalConfig.getStaticUrl("user/default/avatar/b082833e5c59a309880eca3d525e7cae.gif");

    @Column(length = 20)
    private String password;

    @Column(length = 50, nullable = false)
    private String registerTime;

    @Column(length = 50, nullable = false)
    private String latestLoginTime;

    //    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;

    //    @JsonBackReference
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    //    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes;

    @Override
    public String toString() {
        Map<String, Object> dict = new HashMap<>();
        dict.put("id", getId());
        dict.put("nickname", getNickname());
        dict.put("email", getEmail());
        dict.put("avatar", getAvatar());
        dict.put("password", getPassword());
        dict.put("registerTime", getRegisterTime());
        dict.put("latestLoginTime", getLatestLoginTime());
        return dict.toString();
    }
}



