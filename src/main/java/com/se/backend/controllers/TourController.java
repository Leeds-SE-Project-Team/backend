package com.se.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Tour;
import com.se.backend.models.User;
import com.se.backend.services.TourService;
import com.se.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 创建行程
     *
     * @param form 行程创建表单
     * @return ApiResponse<Void>
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createTour(@RequestAttribute("user") User user, @RequestBody TourService.CreateTourForm form) throws JsonProcessingException {
        tourService.createTour(user, form);
        return ApiResponse.success("Create tour succeed");
    }

    /**
     * 更新用户信息
     *
     * @param user            用户
     * @param updatedTourInfo 更新后的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping
    ApiResponse<Tour> updateTour(@RequestAttribute("user") User user, @RequestBody TourService.UpdateTourForm updatedTourInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourService.updateTour(user.getId(), updatedTourInfo));
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

}


