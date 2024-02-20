package com.se.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.se.backend.models.User;
import com.se.backend.services.TourCollectionService;
import com.se.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/tour_collection")
public class TourCollectionController {

    private TourCollectionService tourCollectionService;

    @Autowired
    public TourCollectionController(TourCollectionService tourCollectionService) {
        this.tourCollectionService = tourCollectionService;
    }

    /**
     * 创建行程集合
     *
     * @param form 行程创建集合表单
     * @return ApiResponse<Void>
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createTourCollection(@RequestAttribute("user") User user, @RequestBody TourCollectionService.CreateTourCollectionForm form) throws JsonProcessingException {
        tourCollectionService.createTourCollection(user, form);
        return ApiResponse.success("Create tour collection succeed");
    }

}
