package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.TourCollectionDTO;
import com.se.backend.services.TourCollectionService;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @eo.api-type http
 * @eo.groupName TourCollection
 * @eo.path /tour_collection
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/tour_collection")
public class TourCollectionController {


    private final TourCollectionService tourCollectionService;
    private final UserService userService;

    @Autowired
    public TourCollectionController(TourCollectionService tourCollectionService, UserService userService) {
        this.tourCollectionService = tourCollectionService;
        this.userService = userService;
    }


    /**
     * @param user
     * @param form
     * @return ApiResponse
     * @eo.name createTourCollection
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<TourCollectionDTO> createTourCollection(@RequestAttribute("user") User user, @RequestBody TourCollectionService.CreateTourCollectionForm form) throws ResourceException {
        User eagerredUser = userService.getUserById(user.getId());
        return ApiResponse.success("Create tour collection succeed", tourCollectionService.createTourCollection(eagerredUser, form).toDTO());

    }

    /**
     * @return ApiResponse
     * @eo.name getAllTourCollection
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourCollectionDTO>> getAllTourCollection() {
        return ApiResponse.success("Get all tour collections", TourCollectionDTO.toListDTO(tourCollectionService.getAllTourCollections()));
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getTourCollectionByUserId
     * @eo.url /user
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping(value = "/user")
    ApiResponse<List<TourCollectionDTO>> getTourCollectionByUserId(@RequestAttribute("user") User user) {
        return ApiResponse.success("Tour Collections found by user successfully!", TourCollectionDTO.toListDTO(tourCollectionService.getTourCollectionByUser(user)));
    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name getTourCollectionById
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping
    ApiResponse<TourCollectionDTO> getTourCollectionById(@RequestParam Long id) {
        try {
            return ApiResponse.success("Get tour", tourCollectionService.getTourCollectionById(id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


}
