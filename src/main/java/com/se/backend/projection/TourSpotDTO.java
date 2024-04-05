package com.se.backend.projection;

import com.se.backend.models.TourSpot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (Objects.isNull(TourSpotList)) {
            return new ArrayList<>(0);
        }
        return TourSpotList.stream().map(TourSpot::toDTO).toList();
    }
}
