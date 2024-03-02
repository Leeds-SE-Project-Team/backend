package com.se.backend.models;

import com.alibaba.fastjson2.annotation.JSONField;
import com.se.backend.dto.CommentDTO;
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

    @JSONField(name = "tourId")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Tour tour; // 关联到Trip实体

    @JSONField(name = "tourId")
    public Long getTourId() {
        return tour.getId();
    }

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String publishTime;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
//    TODO: CascadeType.REMOVE
    private List<Comment> replies;

    //    @JsonBackReference
    //    @JsonProperty("parentId")
    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Comment parent;

    public CommentDTO toDTO() {
        return new CommentDTO(this);
    }

//    @Override
//    public String toString() {
//        Map<String, Object> commentDict = new HashMap<>();
//        commentDict.put("id", getId());
//        commentDict.put("tourId", getTour().getId());
//        commentDict.put("author", getAuthor());
//        commentDict.put("content", getContent());
//        commentDict.put("publishTime", getPublishTime());
//        commentDict.put("replies", getReplies());
//        Comment parent = getParent();
//        commentDict.put("parentId", Objects.nonNull(parent) ? parent.getId() : null);
//        return commentDict.toString();
//    }
}
