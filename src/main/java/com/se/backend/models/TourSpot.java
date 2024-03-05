package com.se.backend.models;

import com.se.backend.projection.TourSpotDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tour_spot")
@Getter
@Setter
public class TourSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Tour tour;// 关联到Trip实体

    public TourSpotDTO toDTO() {
        return new TourSpotDTO(this);
    }
}