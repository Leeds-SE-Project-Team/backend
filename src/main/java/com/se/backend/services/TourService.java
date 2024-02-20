package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.PON;
import com.se.backend.models.Tour;
import com.se.backend.models.User;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Autowired
    public TourService(TourRepository tourRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    public Tour getTourById(Long tourId) throws ResourceException {
        return tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
    }

    @Getter
    public static class CreateTourForm {
        String startLocation;
        String endLocation;
        Tour.TourType type;
        List<PON> pons;
    }

    public Tour createTour(User user, CreateTourForm form) {
        Tour newTour = new Tour();
        newTour.setType(form.type);
        newTour.setCreateTime(TimeUtil.getCurrentTimeString());
//        newTour.setUser(userRepository.findById(form.userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND)));
        newTour.setUser(user);
        newTour.setPons(form.pons);
        newTour.setStartLocation(form.startLocation);
        newTour.setEndLocation(form.endLocation);
        return tourRepository.saveAndFlush(newTour);
    }

    @Getter
    public static class UpdateTourForm extends CreateTourForm {
    }

    public Tour updateTour(Long id, UpdateTourForm updatedTourInfo) throws ResourceException {
        Tour existingTour = getTourById(id);
        existingTour.setStartLocation(updatedTourInfo.getStartLocation());
        existingTour.setEndLocation(updatedTourInfo.getEndLocation());
        existingTour.setType(updatedTourInfo.getType());
        existingTour.setPons(updatedTourInfo.getPons());

        return tourRepository.save(existingTour);

    }
}
