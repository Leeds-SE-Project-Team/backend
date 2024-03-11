package com.se.backend.projection;

import com.se.backend.models.GroupCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupCollectionDTO {
    Long id;
    GroupDTO group;
    String title;
    String name;
    String coverUrl;
    String description;
    List<TourDTO> tours;

    public GroupCollectionDTO(GroupCollection groupCollection) {
        id = groupCollection.getId();
        group = groupCollection.getGroup().toDTO();
        title = groupCollection.getTitle();
        name = groupCollection.getName();
        coverUrl = groupCollection.getCoverUrl();
        description = groupCollection.getDescription();
        tours = TourDTO.toListDTO(groupCollection.getTours());
    }

    public static List<GroupCollectionDTO> toListDTO(List<GroupCollection> groupCollectionList) {
        return groupCollectionList.stream().map(GroupCollection::toDTO).toList();
    }
}
