package com.se.backend.projection;

import com.se.backend.models.Tour;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TourDTO {
    Long id;
    String startLocation;
    String endLocation;
    Tour.TourType type;
    String createTime;
    List<PONDTO> pons;
    Long tourCollectionId;
    List<TourHighlightDTO> tourHighlightList;
    List<TourSpotDTO> tourSpotList;
    UserDTO user;

    public TourDTO(Tour tour) {
        id = tour.getId();
        startLocation = tour.getStartLocation();
        endLocation = tour.getEndLocation();
        createTime = tour.getCreateTime();
        type = tour.getType();
        pons = PONDTO.toListDTO(tour.getPons());
        tourCollectionId = tour.getTourCollection().getId();
        tourHighlightList = TourHighlightDTO.toListDTO(tour.getHighlights());
        tourSpotList = TourSpotDTO.toListDTO(tour.getSpots());
        user = tour.getUser().toDTO();
    }

    public static List<TourDTO> toListDTO(List<Tour> tourList) {
        if (Objects.isNull(tourList)) {
            return new ArrayList<>(0);
        }
        return tourList.stream().map(Tour::toDTO).toList();
    }
}
