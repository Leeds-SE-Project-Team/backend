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
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    List<Group> groups;

    @ManyToMany(mappedBy = "likedBy")
    Set<Tour> tourLikes;

    @ManyToMany(mappedBy = "starredBy")
    Set<Tour> tourStars;

    @ManyToMany(mappedBy = "likedBy")
    Set<Comment> commentLikes;
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
    @Column(length = 50)
    private String password;
    @Column(length = 50, nullable = false)
    private String registerTime;
    @Column(length = 50, nullable = false)
    private User.UserType type; // 添加出行类型字段
    @Column(length = 50, nullable = false)
    private String latestLoginTime;
    @Column(length = 50)
    private String vipExpireTime;
    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> leadingGroups;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentLike> commentLikes;
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Tour> tours;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourCollection> tourCollections;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours;

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

    @Getter
    public enum VipPackage {
        MONTHLY(6D), QUARTERLY(16D), YEARLY(60D), FOREVER(160D);

        private final String name;
        private final Double amount;

        VipPackage(Double amount) {
            this.name = this.name();
            this.amount = amount;
        }
    }

}



