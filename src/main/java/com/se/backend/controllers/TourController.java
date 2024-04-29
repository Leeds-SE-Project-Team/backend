package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.TourDTO;
import com.se.backend.projection.UserDTO;
import com.se.backend.services.TourService;
import com.se.backend.services.UserService;
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
    private final UserService userService;

    @Autowired
    public TourController(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
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
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("Create tour succeed", tourService.createTour(eagerredUser, form).toDTO());
        } catch (ResourceException | AuthException | IOException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param form
     * @return ApiResponse
     * @eo.name completeTour
     * @eo.url /complete
     * @eo.method post
     * @eo.request-type json
     */
    @PostMapping(value = "/complete")
    ApiResponse<TourDTO> completeTour(@RequestBody TourService.SaveTourForm form) {
        try {
            return ApiResponse.success("Tour completed!", tourService.completeTour(form).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param updatedTourInfo
     * @return ApiResponse
     * @eo.name updateTour
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping
    ApiResponse<TourDTO> updateTour(@RequestBody TourService.UpdateTourForm updatedTourInfo) {
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

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getToursByUser
     * @eo.url /user
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/user")
    ApiResponse<List<TourDTO>> getToursByUser(@RequestAttribute("user") User user) {
        return ApiResponse.success("Get tour", TourDTO.toListDTO(tourService.getToursByUser(user)));
    }

    /**
     * @return ApiResponse
     * @eo.name getWeeklyTour
     * @eo.url /weekly
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
    @GetMapping(value = "/weekly")
    ApiResponse<List<TourService.ContentDataRecord>> getWeeklyTour() {
        return ApiResponse.success("Get tours weekly data", tourService.getWeeklyTour());
    }


    /**
     * @param user
     * @param id
     * @return ApiResponse
     * @eo.name likeTour
     * @eo.url /like
     * @eo.method post
     * @eo.request-type formdata
     */
    @PostMapping("/like")
    public ApiResponse<TourDTO> likeTour(@RequestAttribute("user") User user, @RequestParam Long id) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            TourDTO updatedTour = tourService.likeTour(eagerredUser, id);
            return ApiResponse.success("Tour liked successfully", updatedTour);
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param id
     * @return ApiResponse
     * @eo.name starTour
     * @eo.url /star
     * @eo.method post
     * @eo.request-type formdata
     */
    @PostMapping("/star")
    ApiResponse<TourDTO> starTour(@RequestAttribute("user") User user, @RequestParam Long id) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            TourDTO updatedTour = tourService.starTour(eagerredUser, id);
            return ApiResponse.success("Tour starred successfully", updatedTour);
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param id
     * @return ApiResponse
     * @eo.name cancelLikeTour
     * @eo.url /like
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping("/like")
    ApiResponse<TourDTO> cancelLikeTour(@RequestAttribute("user") User user, @RequestParam Long id) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("Tour like was cancelled successfully", tourService.cancelLikeTour(eagerredUser, id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param id
     * @return ApiResponse
     * @eo.name cancelStarTour
     * @eo.url /star
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping("/star")
    ApiResponse<TourDTO> cancelStarTour(@RequestAttribute("user") User user, @RequestParam Long id) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("Tour star was cancelled successfully", tourService.cancelStarTour(eagerredUser, id).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getAllLikedToursByUserId
     * @eo.url /liked/by-user
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/liked/by-user")
    ApiResponse<List<TourDTO>> getAllLikedToursByUserId(@RequestAttribute("user") User user) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("Retrieved all liked tours", TourDTO.toListDTO(eagerredUser.getTourLikes().stream().toList()));
//            return ApiResponse.success("Retrieved all liked tours", tourService.getAllLikedToursByUserId(user.getId()));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name getAllStarredToursByUserId
     * @eo.url /starred/by-user
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/starred/by-user")
    ApiResponse<List<TourDTO>> getAllStarredToursByUserId(@RequestAttribute("user") User user) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("Retrieved all starred tours", TourDTO.toListDTO(eagerredUser.getTourStars().stream().toList()));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param tourId
     * @return ApiResponse
     * @eo.name getAllUsersByLikedTourId
     * @eo.url /likes/by-tour
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/likes/by-tour")
    ApiResponse<List<UserDTO>> getAllUsersByLikedTourId(@RequestParam Long tourId) {
        try {
            return ApiResponse.success("Users who liked the tour", UserDTO.toListDTO(tourService.getTourById(tourId).getLikedBy().stream().toList()));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param tourId
     * @return ApiResponse
     * @eo.name getAllUsersByStarredTourId
     * @eo.url /stars/by-tour
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/stars/by-tour")
    ApiResponse<List<UserDTO>> getAllUsersByStarredTourId(@RequestParam Long tourId) {
        try {
            return ApiResponse.success("Users who starred the tour", UserDTO.toListDTO(tourService.getTourById(tourId).getStarredBy().stream().toList()));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

//    /**
//     * @param user
//     * @param file
//     * @param uploadURL
//     * @param uploadGpxForm
//     * @return ApiResponse
//     * @eo.url /upload
//     * @eo.method post
//     * @eo.request-type formdata
//     */
//    @PostMapping("/uploadGPXCreatTour")
//    public ApiResponse<TourDTO> gpxTour(@RequestAttribute("user") User user, @RequestParam("file") MultipartFile file, @RequestParam("uploadURL") String uploadURL, @RequestBody TourService.uploadGpxForm uploadGpxForm) {
//        if (file.isEmpty()) {
//            return ApiResponse.error("File is empty");
//        }
//        try {
////            return ApiResponse.success("File uploaded successfully", uploadURL.concat("/").concat(fileName));
//            return ApiResponse.success("Gpx Create Tour", tourService.uploadGPXCreateTour(user, file, uploadGpxForm).toDTO());
//        } catch (Exception e) {
//            return ApiResponse.error(e.getMessage());
//        }
//    }

    /**
     * @param id
     * @return ApiResponse
     * @eo.name deleteTourById
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    /*
     * @param id
     * @return ApiResponse
     * @eo.name deleteTourById
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping
    ApiResponse<Void> deleteTourById(@RequestParam(required = false) Long id) {
        try {
            tourService.deleteTour(id);
            return ApiResponse.success("Tour has been deleted");
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


