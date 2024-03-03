package com.se.backend.models;

import com.se.backend.projection.TourCollectionDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "tour_collection")
@Getter
@Setter
public class TourCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user; // 确保与User实体正确关联p

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverUrl;

    @Column
    private String description;

    @OneToMany(mappedBy = "tourCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours;


    public TourCollectionDTO toDTO() {
        return new TourCollectionDTO(this);
    }
}