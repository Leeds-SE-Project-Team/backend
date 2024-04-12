package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.projection.TourSpotDTO;
import com.se.backend.services.TourSpotService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @eo.api-type http
 * @eo.groupName TourSpot
 * @eo.path /tour_spot
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/tour_spot")
public class TourSpotController {
    private final TourSpotService tourSpotService;

    @Autowired


    public TourSpotController(TourSpotService tourSpotService) {
        this.tourSpotService = tourSpotService;

    }


    /**
     * @eo.name createTourSpot
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     * @param form
     * @return ApiResponse
     */
    @PostMapping(value = "/create")
    ApiResponse<TourSpotDTO> createTourSpot(@RequestBody TourSpotService.CreateTourSpotForm form) {
        try {
            return ApiResponse.success("Create tour spot succeed", tourSpotService.createTourSpot(form).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @eo.name updateTourSpot
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     * @param updatedTourSpotInfo
     * @return ApiResponse
     */
    @PutMapping
    ApiResponse<TourSpotDTO> updateTourSpot(@RequestBody TourSpotService.UpdateTourSpotForm updatedTourSpotInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourSpotService.updateTourSpot(updatedTourSpotInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @eo.name getAllTourSpots
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourSpotDTO>> getAllTourSpots() {
        return ApiResponse.success("Get all tour spots", TourSpotDTO.toListDTO(tourSpotService.getAllTourSpots()));
    }

    /**
     * @eo.name deleteTourSpot
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     * @param id
     * @return ApiResponse
     */
    @DeleteMapping
    ApiResponse<Void> deleteTourSpot(@RequestParam(required = false) Long id) {
        // TODO: 本人身份验证
        try {
            tourSpotService.deleteTourSpot(id);
            return ApiResponse.success("Tour Spot has been deleted");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


