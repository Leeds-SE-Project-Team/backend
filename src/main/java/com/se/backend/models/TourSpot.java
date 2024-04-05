package com.se.backend.models;

import com.se.backend.projection.TourSpotDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "tour_spot")
@Getter
@Setter
public class TourSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tour tour;// 关联到Trip实体

    @OneToMany(mappedBy = "tourSpot", cascade = {CascadeType.REMOVE})
    private List<TourImage> tourImages;


    public TourSpotDTO toDTO() {
        return new TourSpotDTO(this);
    }
}