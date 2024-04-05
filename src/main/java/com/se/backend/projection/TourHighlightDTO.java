package com.se.backend.projection;

import com.se.backend.models.Tour;
import com.se.backend.models.TourHighlight;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TourHighlightDTO {
    Long id;
    String title;
    List<TourImageDTO> tourImages;
    String location;
    List<Long> toursId;

    public TourHighlightDTO(TourHighlight tourHighlight) {
        id = tourHighlight.getId();
        title = tourHighlight.getTitle();
        tourImages = TourImageDTO.toListDTO(tourHighlight.getTourImages());
        location = tourHighlight.getLocation();
        toursId = tourHighlight.getTours().stream().map(Tour::getId).toList();
    }

    public static List<TourHighlightDTO> toListDTO(List<TourHighlight> tourHighlightList) {
        if (Objects.isNull(tourHighlightList)) {
            return new ArrayList<>(0);
        }
        return tourHighlightList.stream().map(TourHighlight::toDTO).toList();
    }
}
