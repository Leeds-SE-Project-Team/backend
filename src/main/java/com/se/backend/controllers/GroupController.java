package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.GroupDTO;

import com.se.backend.projection.TourDTO;
import com.se.backend.projection.TourHighlightDTO;
import com.se.backend.services.GroupService;
import com.se.backend.services.TourHighlightService;

import com.se.backend.services.TourService;
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


    public GroupController( GroupService groupService) {
        this.groupService = groupService;

    }


    /**
     * @eo.name createGroup
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     * @param user
     * @param form
     * @return ApiResponse
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createGroup(@RequestAttribute("user") User user, @RequestBody GroupService.CreateGroupForm form) {
        try {
            groupService.createGroup(user,form);
            return ApiResponse.success("Create group succeed");
        } catch (ResourceException | AuthException | IOException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @eo.name updateGroup
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     * @param user
     * @param updatedGroupInfo
     * @return ApiResponse
     */
    @PutMapping
    ApiResponse<GroupDTO> updateGroup(@RequestAttribute("user") User user, @RequestBody GroupService.UpdateGroupForm updatedGroupInfo) {
        try {
            return ApiResponse.success("Tour information updated", groupService.updateGroup(updatedGroupInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    /**
     * @eo.name getAllGroup
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<GroupDTO>> getAllGroup() {
        return ApiResponse.success("Get all groups", GroupDTO.toListDTO(groupService.getAllGroups()));
    }


    /**
     * @eo.name deleteGroup
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
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


