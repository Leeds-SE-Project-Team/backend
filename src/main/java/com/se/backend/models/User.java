package com.se.backend.models;

import com.se.backend.config.GlobalConfig;
import com.se.backend.projection.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

//spring data JPA

@Entity
@Table(name = "user")
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    public static final String DEFAULT_AVATAR = GlobalConfig.getStaticUrl("user/default/avatar/avatar.jpg");
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    List<Group> groups;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_likes_tour", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tour_id"))
    Set<Tour> tourLikes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_stars_tour", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "star_id"))
    Set<Tour> tourStars;
    // Extra Information
    @Column
    String gender;
    @Column
    Integer age;
    @Column
    Double height;
    @Column
    Double weight;
    @Column
    String location; // "XX省 XX市"
    @Column(length = 50)
    String signature; // 个性签名
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
    private User.UserType type; // 添加出行类型字段
    @Column(length = 50, nullable = false)
    private String latestLoginTime;
    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> leadingGroups;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes;
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Tour> tours;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TourCollection> tourCollections;
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private List<TourLike> tourLikes;
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private List<TourStar> tourStars;

    public UserDTO toDTO() {
        return new UserDTO(this);
    }

    @Getter
    public enum UserType {
        COMMON("common"), VIP("vip"), ADMIN("admin");

        private final String type;

        UserType(String type) {
            this.type = type;
        }
    }
}



