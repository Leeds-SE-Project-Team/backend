package com.se.backend.projection;

import com.se.backend.models.TourSpot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourSpotDTO {
    Long id;
    List<TourImageDTO> tourImages;
    String location;
    Long tourId;

    public TourSpotDTO(TourSpot tourSpot) {
        id = tourSpot.getId();
        tourImages = TourImageDTO.toListDTO(tourSpot.getTourImages());
        location = tourSpot.getLocation();
        tourId = tourSpot.getTour().getId();
    }

    public static List<TourSpotDTO> toListDTO(List<TourSpot> TourSpotList) {
        return TourSpotList.stream().map(TourSpot::toDTO).toList();
    }
}
