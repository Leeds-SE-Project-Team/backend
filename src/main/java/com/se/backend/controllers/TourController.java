package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.TourDTO;
import com.se.backend.services.TourService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @eo.api-type http
 * @eo.groupName Tour
 * @eo.path /tours
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/tours")
public class TourController {
    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }


    /**
     * @eo.name createTour
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     * @param user
     * @param form
     * @return ApiResponse
     */
    @PostMapping(value = "/create")
    ApiResponse<TourDTO> createTour(@RequestAttribute("user") User user, @RequestBody TourService.CreateTourForm form) {
        try {
            return ApiResponse.success("Create tour succeed", tourService.createTour(user, form).toDTO());
        } catch (ResourceException | AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @eo.name updateTour
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     * @param user
     * @param updatedTourInfo
     * @return ApiResponse
     */
    @PutMapping
    ApiResponse<TourDTO> updateTour(@RequestAttribute("user") User user, @RequestBody TourService.UpdateTourForm updatedTourInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourService.updateTour(user.getId(), updatedTourInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    /**
     * @eo.name getAllTour
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourDTO>> getAllTour() {
        return ApiResponse.success("Get all tours", TourDTO.toListDTO(tourService.getAllTours()));
    }

    /**
     * @eo.name getTourById
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping
    ApiResponse<TourDTO> getTourById(@RequestParam Long id) {
        try {
            return ApiResponse.success("Get tour", tourService.getTourById(id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


