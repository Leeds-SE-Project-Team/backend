package com.se.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
class TokenId implements Serializable {
    private User user;
    private String osPlatform;
}

@Entity
@Table(name = "token")
@Getter
@Setter
@IdClass(TokenId.class)
public class Token {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, unique = true)
    private User user;

    @Id
    @Column(length = 50, nullable = false, unique = true)
    private String osPlatform;

    @Column(length = 50, nullable = false, unique = true)
    private String token;

}


//package com.se.backend.models;
//
//import jakarta.persistence.*;
//        import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "token")
//@IdClass(TokenId.class) // 指定使用TokenId类作为复合主键
//@Getter
//@Setter
//public class Token {
//
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Id
//    @Column(name = "os_platform", length = 50, nullable = false)
//    private String osPlatform;
//
//    @Column(length = 50, nullable = false, unique = true)
//    private String token;
//
//    public Token(User user, String osPlatform, String token) {
//        this.user = user;
//        this.osPlatform = osPlatform;
//        this.token = token;
//    }
//
//    public Token() {
//
//    }
//}