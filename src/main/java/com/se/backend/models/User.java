package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

//spring data JPA

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 20)
    private String password;

    @Column(nullable = false)
    private LocalDateTime registerTime;

    @Column(nullable = false)
    private LocalDateTime latestLoginTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
}



