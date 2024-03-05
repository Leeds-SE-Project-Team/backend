package com.se.backend.projection;

import com.se.backend.models.TourSpot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourSpotDTO {
    Long id;
    String title;
    String coverUrl;
    Long tourId;

    public TourSpotDTO(TourSpot tourSpot) {
        id = tourSpot.getId();
        title = tourSpot.getTitle();
        coverUrl = tourSpot.getCoverUrl();
        tourId = tourSpot.getTour().getId();
    }

    public static List<TourSpotDTO> toListDTO(List<TourSpot> TourSpotList) {
        return TourSpotList.stream().map(TourSpot::toDTO).toList();
    }
}
