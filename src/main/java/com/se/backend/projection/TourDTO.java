package com.se.backend.projection;

import com.se.backend.models.Tour;
import com.se.backend.models.PON;
import com.se.backend.models.TourHighlight;
import com.se.backend.models.TourSpot;
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
    int type;
    int state;
    String createTime;
    List<Long> pons; // Assuming only IDs are needed
    Long tourCollectionId;
    List<Long> tourHighlightList; // Assuming only IDs are needed
    List<Long> tourSpotList; // Assuming only IDs are needed
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
        pons = tour.getPons().stream().map(PON::getId).collect(Collectors.toList());
        tourCollectionId = tour.getTourCollection().getId();
        tourHighlightList = tour.getHighlights().stream().map(TourHighlight::getId).collect(Collectors.toList());
        tourSpotList = tour.getSpots().stream().map(TourSpot::getId).collect(Collectors.toList());
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
