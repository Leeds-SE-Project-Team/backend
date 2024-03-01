package com.se.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "comment")
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
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
//    TODO: CascadeType.REMOVE
    private List<Comment> replies;

    @JsonBackReference
    //    @JsonProperty("parentId")
    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Comment parent;

    @Override
    public String toString() {
        Map<String, Object> commentDict = new HashMap<>();
        commentDict.put("id", getId());
        commentDict.put("tourId", getTour().getId());
        commentDict.put("author", getAuthor());
        commentDict.put("content", getContent());
        commentDict.put("publishTime", getPublishTime());
        commentDict.put("replies", getReplies());
        commentDict.put("123", "123");
        Comment parent = getParent();
        commentDict.put("parentId", Objects.nonNull(parent) ? parent.getId() : null);
        System.out.println("666");
        return commentDict.toString();
    }
}
