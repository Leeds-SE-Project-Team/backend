package com.se.backend.projection;

import com.se.backend.models.TourHighlight;
import com.se.backend.models.TourImage;
import com.se.backend.models.TourSpot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TourImageDTO {
    Long id;
    String imageUrl;
    Long tourHighlightId;
    Long tourSpotId;
    Long tourId;

    public TourImageDTO(TourImage tourImage) {
        id = tourImage.getId();
        TourHighlight tourHighlight = tourImage.getTourHighlight();
        if (Objects.nonNull(tourHighlight)) {
            tourHighlightId = tourHighlight.getId();
        }
        TourSpot tourSpot = tourImage.getTourSpot();
        if (Objects.nonNull(tourSpot)) {
            tourSpotId = tourSpot.getId();
        }
        imageUrl = tourImage.getImageUrl();
        tourId = tourImage.getTour().getId();
    }

    public static List<TourImageDTO> toListDTO(List<TourImage> tourImageList) {
        return Objects.nonNull(tourImageList) ? tourImageList.stream().map(TourImage::toDTO).toList() : new ArrayList<>();
    }
}
