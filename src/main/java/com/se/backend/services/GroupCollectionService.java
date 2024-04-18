package com.se.backend.services;


import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.GroupCollection;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.models.Group;
import com.se.backend.repositories.GroupCollectionRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.GROUP_COLLECTION_NOT_FOUND;

@Service
public class GroupCollectionService {
    private final GroupCollectionRepository groupCollectionRepository;

    @Autowired
    public GroupCollectionService( GroupCollectionRepository groupCollectionRepository) {
        this.groupCollectionRepository = groupCollectionRepository;


    }

    public GroupCollection getGroupCollectionById(Long groupCollectionId) throws ResourceException {
        return groupCollectionRepository.findById(groupCollectionId).orElseThrow(() -> new ResourceException(GROUP_COLLECTION_NOT_FOUND));
    }

    public List<GroupCollection> getAllGroupCollections() {
        return groupCollectionRepository.findAll();
    }
    public List<GroupCollection> getGroupCollectionByUser(User user) {
        return groupCollectionRepository.findAllByUser(user);
    }
    public List<GroupCollection> getGroupCollectionByGroup(Group group) {
        return groupCollectionRepository.findAllByGroup(group);
    }

    public GroupCollection createGroupCollection(Group group, GroupCollectionService.CreateGroupCollectionForm form) {
        GroupCollection newGroupCollection = new GroupCollection();
        newGroupCollection.setGroup(group);
        newGroupCollection.setName(form.name);
        newGroupCollection.setTitle(form.title);
        newGroupCollection.setCoverUrl(form.coverUrl);
        newGroupCollection.setDescription(form.description);
        return groupCollectionRepository.saveAndFlush(newGroupCollection);
    }

    @Getter
    @AllArgsConstructor
    public static class CreateGroupCollectionForm {
        String name;
        String title;
        String coverUrl;
        String description;
    }


}
