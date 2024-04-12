package com.se.backend.controllers;

import com.se.backend.models.User;
import com.se.backend.projection.TourCollectionDTO;
import com.se.backend.services.TourCollectionService;
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

    @Autowired
    public TourCollectionController(TourCollectionService tourCollectionService) {
        this.tourCollectionService = tourCollectionService;
    }


    /**
     * @eo.name createTourCollection
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     * @param user
     * @param form
     * @return ApiResponse
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createTourCollection(@RequestAttribute("user") User user, @RequestBody TourCollectionService.CreateTourCollectionForm form) {
        tourCollectionService.createTourCollection(user, form);
        return ApiResponse.success("Create tour collection succeed");
    }

    /**
     * @eo.name getAllTourCollection
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourCollectionDTO>> getAllTourCollection() {
        return ApiResponse.success("Get all tour collections", TourCollectionDTO.toListDTO(tourCollectionService.getAllTourCollections()));
    }

    /**
     * @eo.name getTourCollectionByUserId
     * @eo.url /user
     * @eo.method get
     * @eo.request-type formdata
     * @param user
     * @return ApiResponse
     */
    @GetMapping(value = "/user")
    ApiResponse<List<TourCollectionDTO>> getTourCollectionByUserId(@RequestAttribute("user") User user) {
        return ApiResponse.success("Tour Collections found by user successfully!", TourCollectionDTO.toListDTO(tourCollectionService.getTourCollectionByUser(user)));
    }


}
