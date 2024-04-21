package com.se.backend.services;


import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Group;
import com.se.backend.models.GroupCollection;
import com.se.backend.repositories.GroupCollectionRepository;
import com.se.backend.repositories.GroupRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.se.backend.exceptions.ResourceException.ErrorType.GROUP_COLLECTION_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.GROUP_NOT_FOUND;

@Service
public class GroupCollectionService {
    private final GroupCollectionRepository groupCollectionRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public GroupCollectionService(GroupCollectionRepository groupCollectionRepository, GroupRepository groupRepository) {
        this.groupCollectionRepository = groupCollectionRepository;


        this.groupRepository = groupRepository;
    }

    public GroupCollection getGroupCollectionById(Long groupCollectionId) throws ResourceException {
        return groupCollectionRepository.findById(groupCollectionId).orElseThrow(() -> new ResourceException(GROUP_COLLECTION_NOT_FOUND));
    }

    public List<GroupCollection> getAllGroupCollections() {
        return groupCollectionRepository.findAll();
    }

    //    public List<GroupCollection> getGroupCollectionByUser(User user) {
//        user.getG
//        return groupCollectionRepository.findAllByUser(user);
//    }
    public List<GroupCollection> getGroupCollectionByGroup(Group group) {
        return groupCollectionRepository.findAllByGroup(group);
    }

    public GroupCollection createGroupCollection( GroupCollectionService.CreateGroupCollectionForm form) throws ResourceException {
        GroupCollection newGroupCollection = new GroupCollection();
//        newGroupCollection.setGroup(group);
        newGroupCollection.setName(form.name);
        newGroupCollection.setTitle(form.title);
        newGroupCollection.setCoverUrl(form.coverUrl);
        newGroupCollection.setDescription(form.description);

        if(Objects.nonNull(form.groupId)){
            Group existingGroup= groupRepository.findById(form.groupId).orElseThrow(()->new ResourceException(GROUP_NOT_FOUND));
            newGroupCollection.setGroup(existingGroup);
        }

        return groupCollectionRepository.saveAndFlush(newGroupCollection);
    }

    @Getter
    @AllArgsConstructor
    public static class CreateGroupCollectionForm {
        Long groupId;
        String name;
        String title;
        String coverUrl;
        String description;
    }


}
