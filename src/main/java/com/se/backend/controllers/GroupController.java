package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.GroupDTO;
import com.se.backend.projection.TourDTO;
import com.se.backend.services.GroupService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


/**
 * @eo.api-type http
 * @eo.groupName Group
 * @eo.path /groups
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired


    public GroupController(GroupService groupService) {
        this.groupService = groupService;

    }


    /**
     * @param user
     * @param form
     * @return ApiResponse
     * @eo.name createGroup
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<GroupDTO> createGroup(@RequestAttribute("user") User user, @RequestBody GroupService.CreateGroupForm form) {
        try {
            return ApiResponse.success("Create group succeed", groupService.createGroup(user, form).toDTO());
        } catch (ResourceException | AuthException | IOException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param updatedGroupInfo
     * @return ApiResponse
     * @eo.name updateGroup
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping
    ApiResponse<GroupDTO> updateGroup(@RequestBody GroupService.UpdateGroupForm updatedGroupInfo) {
        try {
            return ApiResponse.success("Tour information updated", groupService.updateGroup(updatedGroupInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    /**
     * @return ApiResponse
     * @eo.name getAllGroup
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<GroupDTO>> getAllGroup() {
        return ApiResponse.success("Get all groups", GroupDTO.toListDTO(groupService.getAllGroups()));
    }

    /**
     * @eo.name getGroupById
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping
    ApiResponse<GroupDTO> getGroupById(@RequestParam Long id) {
        try {
            return ApiResponse.success("Get group", groupService.getGroupById(id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getAllCreatedGroupsByUser
     * @eo.url /createdByUser
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/createdByUser")
    ApiResponse<List<GroupDTO>> getAllCreatedGroupsByUser(@RequestAttribute("user") User user) {
        return ApiResponse.success("Group created by user found successfully!", GroupDTO.toListDTO(groupService.getAllCreatedGroupsByUser(user)));
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getAllJoinedGroupsByUser
     * @eo.url /joinedByUser
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/joinedByUser")
    ApiResponse<List<GroupDTO>> getAllJoinedGroupsByUser(@RequestAttribute("user") User user) {
        return ApiResponse.success("Group joined by user found successfully!", GroupDTO.toListDTO(groupService.getAllJoinedGroupsByUser(user)));
    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name deleteGroup
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping
    ApiResponse<Void> deleteGroup(@RequestParam(required = false) Long id) {
        // TODO: 本人身份验证
        try {
            groupService.deleteGroup(id);
            return ApiResponse.success("Group has been deleted");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


}


