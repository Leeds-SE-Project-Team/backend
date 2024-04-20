package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Group;
import com.se.backend.projection.GroupCollectionDTO;
import com.se.backend.services.GroupCollectionService;
import com.se.backend.services.GroupService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @eo.api-type http
 * @eo.groupName GroupCollection
 * @eo.path /group_collection
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/group_collection")
public class GroupCollectionController {


    private final GroupCollectionService groupCollectionService;
    private final GroupService groupService;

    @Autowired
    public GroupCollectionController(GroupCollectionService groupCollectionService, GroupService groupService) {
        this.groupCollectionService = groupCollectionService;
        this.groupService = groupService;
    }


    /**
     * @param group
     * @param form
     * @return ApiResponse
     * @eo.name createGroupCollection
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createGroupCollection(@RequestBody Group group, GroupCollectionService.CreateGroupCollectionForm form) {
        groupCollectionService.createGroupCollection(group, form);
        return ApiResponse.success("Create tour collection succeed");
    }


    /**
     * @return ApiResponse
     * @eo.name getAllGroupCollections
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<GroupCollectionDTO>> getAllGroupCollections() {
        return ApiResponse.success("Get all group collections", GroupCollectionDTO.toListDTO(groupCollectionService.getAllGroupCollections()));
    }

//    /**
//     * @eo.name getGroupCollectionByUser
//     * @eo.url /user
//     * @eo.method get
//     * @eo.request-type formdata
//     * @param user
//     * @return ApiResponse
//     */
//    @GetMapping(value = "/user")
//    ApiResponse<List<GroupCollectionDTO>> getGroupCollectionByUser(@RequestAttribute("user") User user) {
//        return ApiResponse.success("Group Collections found by user successfully!", GroupCollectionDTO.toListDTO(groupCollectionService.getGroupCollectionByUser(user)));
//    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name getGroupCollectionByGroup
     * @eo.url /group
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping(value = "/group")
    ApiResponse<List<GroupCollectionDTO>> getGroupCollectionByGroup(@RequestParam(required = false) Long id) {
        try {
            return ApiResponse.success("Group Collections found by group successfully!", GroupCollectionDTO.toListDTO(groupCollectionService.getGroupCollectionByGroup(groupService.getGroupById(id))));
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


}
