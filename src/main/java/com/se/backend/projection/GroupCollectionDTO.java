package com.se.backend.projection;

import com.se.backend.models.GroupCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class GroupCollectionDTO {
    Long id;
    Long groupId;
    String title;
    String name;
    String coverUrl;
    String description;
    List<TourDTO> tours;

    public GroupCollectionDTO(GroupCollection groupCollection) {
        id = groupCollection.getId();
        groupId = groupCollection.getGroup().getId();
        title = groupCollection.getTitle();
        name = groupCollection.getName();
        coverUrl = groupCollection.getCoverUrl();
        description = groupCollection.getDescription();
        tours = TourDTO.toListDTO(groupCollection.getTours());
    }

    public static List<GroupCollectionDTO> toListDTO(List<GroupCollection> groupCollectionList) {
        if (Objects.isNull(groupCollectionList)) {
            return new ArrayList<>(0);
        }
        return groupCollectionList.stream().map(GroupCollection::toDTO).toList();
    }
}
