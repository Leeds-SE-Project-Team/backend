package com.se.backend.models;

import com.se.backend.projection.TourRecordDataDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tour_record_data")
@Getter
@Setter
public class TourRecordData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double avgSpeed;

    @Column
    private Double totalDistance;

    @Column
    private Double timeInMotion;

    @Column
    private Double timeTaken;

    @Column
    private Double calorie;

    public TourRecordDataDTO toDTO() {
        return new TourRecordDataDTO(this);
    }
}
