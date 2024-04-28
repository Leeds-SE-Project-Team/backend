package com.se.backend.projection;

import com.se.backend.models.TourRecordData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourRecordDataDTO {
    private Long id;
    private Double avgSpeed;
    private Double totalDistance;
    private Double timeInMotion;
    private Double timeTaken;
    private Double calorie;

    public TourRecordDataDTO(TourRecordData tourRecordData) {
        id = tourRecordData.getId();
        avgSpeed = tourRecordData.getAvgSpeed();
        totalDistance = tourRecordData.getTotalDistance();
        timeInMotion = tourRecordData.getTimeInMotion();
        timeTaken = tourRecordData.getTimeTaken();
        calorie = tourRecordData.getCalorie();
    }
}
