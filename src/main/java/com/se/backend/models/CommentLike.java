package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "comment_like")
@Getter
@Setter
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private Comment comment;

    @Column(length = 50, nullable = false)
    private String time;
}
