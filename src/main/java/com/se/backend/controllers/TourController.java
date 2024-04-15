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

import java.io.IOException;
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
     * @param user
     * @param form
     * @return ApiResponse
     * @eo.name createTour
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<TourDTO> createTour(@RequestAttribute("user") User user, @RequestBody TourService.CreateTourForm form) {
        try {
            return ApiResponse.success("Create tour succeed", tourService.createTour(user, form).toDTO());
        } catch (ResourceException | AuthException | IOException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param user
     * @param updatedTourInfo
     * @return ApiResponse
     * @eo.name updateTour
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping
    ApiResponse<TourDTO> updateTour(@RequestAttribute("user") User user, @RequestBody TourService.UpdateTourForm updatedTourInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourService.updateTour(updatedTourInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    /**
     * @return ApiResponse
     * @eo.name getAllTour
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourDTO>> getAllTour() {
        return ApiResponse.success("Get all tours", TourDTO.toListDTO(tourService.getAllTours()));
    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name getTourById
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
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

    @GetMapping("/user")
    ApiResponse<List<TourDTO>> getToursByUser(@RequestAttribute("user") User user) {
        return ApiResponse.success("Get tour", TourDTO.toListDTO(tourService.getToursByUser(user)));
    }
}


