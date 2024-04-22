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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tour tour; // 移除级联删除，防止删除Comment时影响Tour

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;// 移除级联删除，防止删除Comment时影响User

    @Column(nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String publishTime;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> replies; // 仅在移除父评论时删除子评论

    @ManyToOne
    @JoinColumn
    private Comment parent; // 没有级联操作，删除子评论不影响父评论

    public CommentDTO toDTO() {
        return new CommentDTO(this);
    }

}
