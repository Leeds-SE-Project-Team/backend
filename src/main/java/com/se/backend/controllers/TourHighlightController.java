package com.se.backend.controllers;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.projection.TourHighlightDTO;
import com.se.backend.services.TourHighlightService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 创建行程
     *
     * @param form 行程创建表单
     * @return ApiResponse<Void>
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


    @PutMapping
    ApiResponse<TourHighlightDTO> updateTourHighlight(@RequestBody TourHighlightService.UpdateTourHighlightForm updatedTourHighlightInfo) {
        try {
            return ApiResponse.success("Highlight information updated", tourHighlightService.updateTourHighlight(updatedTourHighlightInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourHighlightDTO>> getAllTourHighlights() {
        return ApiResponse.success("Get all tour highlights", TourHighlightDTO.toListDTO(tourHighlightService.getAllTourHighlights()));
    }

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


