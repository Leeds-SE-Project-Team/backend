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
    ApiResponse<TourDTO> createTour(@RequestAttribute("user") User user, @RequestBody TourService.CreateTourForm form) {
        try {
            return ApiResponse.success("Create tour succeed", tourService.createTour(user, form).toDTO());
        } catch (ResourceException | AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     *
     * @param user            用户
     * @param updatedTourInfo 更新后的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping
    ApiResponse<TourDTO> updateTour(@RequestAttribute("user") User user, @RequestBody TourService.UpdateTourForm updatedTourInfo) {
        try {
            return ApiResponse.success("Tour information updated", tourService.updateTour(user.getId(), updatedTourInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourDTO>> getAllTour() {
        return ApiResponse.success("Get all tours", TourDTO.toListDTO(tourService.getAllTours()));
    }

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


