package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.TourSpot;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.TourSpotRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_SPOT_NOT_FOUND;

@Service
public class TourSpotService {

    public final TourSpotRepository tourSpotRepository;
    public final TourRepository tourRepository;

    public TourSpotService(TourSpotRepository tourSpotRepository, TourRepository tourRepository) {
        this.tourSpotRepository = tourSpotRepository;
        this.tourRepository = tourRepository;
    }

    public List<TourSpot> getAllTourSpots() {
        return tourSpotRepository.findAll();
    }

    public TourSpot getTourSpotById(Long tourSpotId) throws ResourceException {
        return tourSpotRepository.findById(tourSpotId).orElseThrow(() -> new ResourceException(TOUR_SPOT_NOT_FOUND));
    }

    public TourSpot createTourSpot(CreateTourSpotForm form) throws ResourceException {
        TourSpot newSpot = new TourSpot();
        newSpot.setTour(tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
        newSpot.setCoverUrl(form.coverUrl);
        newSpot.setTitle(form.title);
        return newSpot;
    }

    public TourSpot updateTourSpot(UpdateTourSpotForm form) throws ResourceException {
        TourSpot existingSpot = getTourSpotById(form.tourSpotId);
        existingSpot.setTour(tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
        existingSpot.setTitle(form.title);
        existingSpot.setCoverUrl(form.coverUrl);
        return tourSpotRepository.saveAndFlush(existingSpot);
    }

    public void deleteTourSpot(Long tourSpotId) throws ResourceException {
        tourSpotRepository.delete(getTourSpotById(tourSpotId));
    }

    @Getter
    public static class CreateTourSpotForm {
        String title;
        String coverUrl;
        Long tourId;
    }

    @Getter
    public static class UpdateTourSpotForm extends CreateTourSpotForm {
        Long tourSpotId;
    }
}
