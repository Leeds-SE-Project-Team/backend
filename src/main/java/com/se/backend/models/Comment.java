package com.se.backend.models;

import com.se.backend.projection.CommentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Tour tour; // 关联到Trip实体

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String publishTime;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Comment parent;

    public CommentDTO toDTO() {
        return new CommentDTO(this);
    }

}
