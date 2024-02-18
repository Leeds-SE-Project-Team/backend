package com.se.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    @JoinColumn(nullable = false)
    private User user;

    @Id
    @Column(length = 50, nullable = false)
    private String osPlatform;

    @Column(nullable = false, unique = true)
    private String token;

}
