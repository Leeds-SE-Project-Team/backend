package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.projection.TourSpotDTO;
import com.se.backend.services.TourSpotService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 创建行程
     *
     * @param form 行程创建表单
     * @return ApiResponse<Void>
     */
    @PostMapping(value = "/create")
    ApiResponse<TourSpotDTO> createTourSpot(@RequestBody TourSpotService.CreateTourSpotForm form) {
        try {
            return ApiResponse.success("Create tour spot succeed", tourSpotService.createTourSpot(form).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    @PutMapping
    ApiResponse<TourSpotDTO> updateTourSpot(@RequestBody TourSpotService.UpdateTourSpotForm updatedTourSpotInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourSpotService.updateTourSpot(updatedTourSpotInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourSpotDTO>> getAllTourSpots() {
        return ApiResponse.success("Get all tour spots", TourSpotDTO.toListDTO(tourSpotService.getAllTourSpots()));
    }

    @DeleteMapping
    ApiResponse<Void> deleteTourSpot(@RequestParam(required = false) Long id) {
        try {
            tourSpotService.deleteTourSpot(id);
            return ApiResponse.success("Tour Spot has been deleted");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


