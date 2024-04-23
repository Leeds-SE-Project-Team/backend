package com.se.backend.projection;

import com.se.backend.models.Tour;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class TourDTO {
    Long id;
    String startLocation;
    String endLocation;
    String title;
    String mapUrl;
    String dataUrl;
    String completeUrl;
    int type;
    int state;
    String createTime;
    List<PONDTO> pons; // Assuming only IDs are needed
    Long tourCollectionId;
    List<TourHighlightDTO> tourHighlightList;
    List<TourSpotDTO> tourSpotList; // Assuming only IDs are needed
    UserDTO user;
    int status;
    List<Long> likedBy; // User IDs who liked this tour
    List<Long> starredBy; // User IDs who starred this tour

    public TourDTO(Tour tour) {
        id = tour.getId();
        startLocation = tour.getStartLocation();
        endLocation = tour.getEndLocation();
        createTime = tour.getCreateTime();
        title = tour.getTitle();
        mapUrl = tour.getMapUrl();
        dataUrl = tour.getDataUrl();
        completeUrl = tour.getCompleteUrl();
        type = tour.getType().ordinal();
        state = tour.getState().ordinal();
        pons = PONDTO.toListDTO(tour.getPons());
        tourCollectionId = tour.getTourCollection().getId();
        tourHighlightList = TourHighlightDTO.toListDTO(tour.getHighlights());
        tourSpotList = TourSpotDTO.toListDTO(tour.getSpots());
        user = tour.getUser().toDTO();
        status = tour.getStatus().ordinal();
        var likedByRecords = tour.getLikes();
        likedBy = Objects.nonNull(likedByRecords) ? likedByRecords.stream().map(like -> like.getUser().getId()).collect(Collectors.toList()) : null;
        var starredByRecords = tour.getStars();
        starredBy = Objects.nonNull(starredByRecords) ? tour.getStars().stream().map(star -> star.getUser().getId()).collect(Collectors.toList()) : null;
    }

    public static List<TourDTO> toListDTO(List<Tour> tourList) {
        if (Objects.isNull(tourList)) {
            return new ArrayList<>(0);
        }
        return tourList.stream().map(Tour::toDTO).toList();
    }
}
