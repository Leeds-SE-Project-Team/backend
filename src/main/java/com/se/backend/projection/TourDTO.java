package com.se.backend.projection;

import com.se.backend.models.Tour;
import lombok.Getter;
import lombok.Setter;
import com.se.backend.models.TourLike;
import com.se.backend.models.TourStar;
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
    int type;
    int state;
    String createTime;
    List<PONDTO> pons;
    Long tourCollectionId;
    List<TourHighlightDTO> tourHighlightList;
    List<TourSpotDTO> tourSpotList;
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
        type = tour.getType().ordinal();
        state = tour.getState().ordinal();
        pons = PONDTO.toListDTO(tour.getPons());
        tourCollectionId = tour.getTourCollection().getId();
        tourHighlightList = TourHighlightDTO.toListDTO(tour.getHighlights());
        tourSpotList = TourSpotDTO.toListDTO(tour.getSpots());
        user = tour.getUser().toDTO();
        status = tour.getStatus().ordinal();
        likedBy = tour.getLikes().stream().map(like -> like.getUser().getId()).collect(Collectors.toList());
        starredBy = tour.getStars().stream().map(star -> star.getUser().getId()).collect(Collectors.toList());
    }

    public static List<TourDTO> toListDTO(List<Tour> tourList) {
        if (Objects.isNull(tourList)) {
            return new ArrayList<>(0);
        }
        return tourList.stream().map(Tour::toDTO).toList();
    }
}
