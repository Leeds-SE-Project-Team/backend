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
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Id
    @Column(length = 50, nullable = false)
    private String osPlatform;

    @Column(nullable = false, unique = true)
    private String token;

}
