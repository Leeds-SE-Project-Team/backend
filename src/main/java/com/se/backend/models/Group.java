package com.se.backend.models;

import com.se.backend.projection.GroupDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {
    @ManyToMany(mappedBy = "groups")
    List<User> members;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User leader;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String coverUrl;
    @Column
    private String description;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupCollection> groupCollections;

    public GroupDTO toDTO() {
        return new GroupDTO(this);
    }
}