package com.se.backend.services;


import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.*;
import com.se.backend.repositories.GroupCollectionRepository;
import com.se.backend.repositories.GroupRepository;

import com.se.backend.repositories.UserRepository;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.*;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupCollectionRepository groupCollectionRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupCollectionRepository groupCollectionRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupCollectionRepository = groupCollectionRepository;
        this.userRepository = userRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }


    public Group getGroupById(Long groupId) throws ResourceException {
        return groupRepository.findById(groupId).orElseThrow(() -> new ResourceException(GROUP_NOT_FOUND));
    }

    public void createGroup(User user, CreateGroupForm form) throws ResourceException, AuthException, IOException {
        Group newGroup = new Group();
//        newGroup.setMembers((List<User>) user);
        newGroup.setName(form.name);
        newGroup.setDescription(form.description);
        newGroup.setCoverUrl(form.coverUrl);

        User existingUser = userRepository.findById(form.leaderId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        existingUser.getGroups().add(newGroup);

    }

    public Group updateGroup(UpdateGroupForm form) throws ResourceException {
        Group existingGroup=getGroupById(form.groupId);
        existingGroup.setName(form.name);
        existingGroup.setDescription(form.description);
        existingGroup.setCoverUrl(form.coverUrl);

        return groupRepository.saveAndFlush(existingGroup);
    }

    public List<Group> getGroupByUser(User user) {
        return groupRepository.findAllByUser(user);
    }

    public void deleteGroup(Long groupId) throws ResourceException {
        Group groupToDelete = getGroupById(groupId);

        // Unlink the highlight from any tours
        List<User> associatedUsers = groupToDelete.getMembers();
        if (associatedUsers != null) {
            for (User user : associatedUsers) {
                user.getGroups().remove(groupToDelete);
                userRepository.save(user); // Update each tour after removing the highlight
            }
        }

        // Delete associated images if needed
        List<GroupCollection> associatedgroupCollections = groupCollectionRepository.findByGroup(groupToDelete);
        if (associatedgroupCollections != null) {
            groupCollectionRepository.deleteAll(associatedgroupCollections); // Or handle them according to your policy
        }

        // Now it is safe to delete the highlight
        groupRepository.delete(groupToDelete);
    }

    @Getter
    public static class CreateGroupForm {

        Long leaderId;
        String name;
        String coverUrl;
        String description;
//        List<GroupCollection> groupCollection

    }

    @Getter
    public static class UpdateGroupForm extends CreateGroupForm {
        Long groupId;
         }
    //CreateGroupCollection
    //AddUserToGroup
    //修改tourCollection get请求   get group collection
    // DeleteUserFromGroup
    //deleteGroup
    //getGroupUser->user...V
    //getGroupDetails->group groupCollection user...V
}
