package com.se.backend.projection;

import com.se.backend.models.TourCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TourCollectionDTO {
    Long id;
    UserDTO user;
    String title;
    String name;
    String coverUrl;
    String description;
    List<TourDTO> tours;

    public TourCollectionDTO(TourCollection tourCollection) {
        id = tourCollection.getId();
        user = tourCollection.getUser().toDTO();
        title = tourCollection.getTitle();
        name = tourCollection.getName();
        coverUrl = tourCollection.getCoverUrl();
        description = tourCollection.getDescription();
        tours = TourDTO.toListDTO(tourCollection.getTours());
    }

    public static List<TourCollectionDTO> toListDTO(List<TourCollection> tourCollectionList) {
        if (Objects.isNull(tourCollectionList)) {
            return new ArrayList<>(0);
        }
        return tourCollectionList.stream().map(TourCollection::toDTO).toList();
    }
}
