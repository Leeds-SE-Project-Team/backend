package com.se.backend.models;

import com.se.backend.config.GlobalConfig;
import com.se.backend.projection.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//spring data JPA

@Entity
@Table(name = "user")
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    public static final String DEFAULT_AVATAR = GlobalConfig.getStaticUrl("user/default/avatar/b082833e5c59a309880eca3d525e7cae.gif");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String nickname;
    @Column(length = 50, unique = true)
    private String email;
    @Column(nullable = false)
    private String avatar;
    @Column(length = 20)
    private String password;

    @Column(length = 50, nullable = false)
    private String registerTime;

    @Column(length = 50, nullable = false)
    private String latestLoginTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes;

    public UserDTO toDTO() {
        return new UserDTO(this);
    }
}



