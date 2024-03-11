package com.se.backend.models;

import com.se.backend.projection.GroupCollectionDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "group_collection")
@Getter
@Setter
public class GroupCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Group group;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String coverUrl;

    @Column
    private String description;

    @OneToMany(mappedBy = "groupCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours;


    public GroupCollectionDTO toDTO() {
        return new GroupCollectionDTO(this);
    }
}