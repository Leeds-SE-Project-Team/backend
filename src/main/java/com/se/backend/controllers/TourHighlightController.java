package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.projection.TourHighlightDTO;
import com.se.backend.services.TourHighlightService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @eo.api-type http
 * @eo.groupName TourHighlight
 * @eo.path /tour_highlight
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/tour_highlight")
public class TourHighlightController {
    private final TourHighlightService tourHighlightService;

    @Autowired


    public TourHighlightController(TourHighlightService tourHighlightService) {
        this.tourHighlightService = tourHighlightService;


    }


    /**
     * @param form
     * @return ApiResponse
     * @eo.name createTourHighlight
     * @eo.url /create
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createTourHighlight(@RequestBody TourHighlightService.CreateTourHighlightForm form) {
        try {
            tourHighlightService.createTourHighlight(form);
            return ApiResponse.success("Create tour highlight succeed");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param updatedTourHighlightInfo
     * @return ApiResponse
     * @eo.name updateTourHighlight
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping
    ApiResponse<TourHighlightDTO> updateTourHighlight(@RequestBody TourHighlightService.UpdateTourHighlightForm updatedTourHighlightInfo) {
        try {
            return ApiResponse.success("Highlight information updated", tourHighlightService.updateTourHighlight(updatedTourHighlightInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @return ApiResponse
     * @eo.name getAllTourHighlights
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourHighlightDTO>> getAllTourHighlights() {
        return ApiResponse.success("Get all tour highlights", TourHighlightDTO.toListDTO(tourHighlightService.getAllTourHighlights()));
    }


    /**
     * @param id
     * @return ApiResponse
     * @eo.name getTourHighlightById
     * @eo.url
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping
    ApiResponse<TourHighlightDTO> getTourHighlightById(@RequestParam(required = false) Long id) {
        try {
            return ApiResponse.success("Get tour highlight", tourHighlightService.getTourHighlightById(id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name deleteTourHighlight
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping
    ApiResponse<Void> deleteTourHighlight(@RequestParam(required = false) Long id) {
        // TODO: 本人身份验证
        try {
            tourHighlightService.deleteTourHighlight(id);
            return ApiResponse.success("Tour Highlight has been deleted");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


