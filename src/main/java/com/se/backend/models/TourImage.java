package com.se.backend.models;

import com.se.backend.projection.TourImageDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tour_image")
@Getter
@Setter
public class TourImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private TourSpot tourSpot;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private TourHighlight tourHighlight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Tour tour;

    public TourImageDTO toDTO() {
        return new TourImageDTO(this);
    }
}