package com.se.backend.projection;

import com.se.backend.models.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class GroupDTO {
    Long id;
    List<UserDTO> members;
    String name;
    String coverUrl;
    String description;
    List<GroupCollectionDTO> groupCollections;

    public GroupDTO(Group group) {
        id = group.getId();
        members = UserDTO.toListDTO(group.getMembers());
        name = group.getName();
        coverUrl = group.getCoverUrl();
        description = group.getDescription();
        groupCollections = GroupCollectionDTO.toListDTO(group.getGroupCollections());
    }

    public static List<GroupDTO> toListDTO(List<Group> groupList) {
        if (Objects.isNull(groupList)) {
            return new ArrayList<>(0);
        }
        return groupList.stream().map(Group::toDTO).toList();
    }
}
