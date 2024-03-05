package com.se.backend.projection;

import com.se.backend.models.TourCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
        return tourCollectionList.stream().map(TourCollection::toDTO).toList();
    }
}
