package com.se.backend.controllers;

import com.se.backend.models.User;
import com.se.backend.projection.TourCollectionDTO;
import com.se.backend.services.TourCollectionService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 创建行程集合
     *
     * @param form 行程创建集合表单
     * @return ApiResponse<Void>
     */
    @PostMapping(value = "/create")
    ApiResponse<Void> createTourCollection(@RequestAttribute("user") User user, @RequestBody TourCollectionService.CreateTourCollectionForm form) {
        tourCollectionService.createTourCollection(user, form);
        return ApiResponse.success("Create tour collection succeed");
    }

    @IgnoreToken
    @GetMapping(value = "/all")
    ApiResponse<List<TourCollectionDTO>> getAllTourCollection() {
        return ApiResponse.success("Get all tour collections", TourCollectionDTO.toListDTO(tourCollectionService.getAllTourCollections()));
    }

}
