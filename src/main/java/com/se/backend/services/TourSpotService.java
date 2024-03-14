package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.TourImage;
import com.se.backend.models.TourSpot;
import com.se.backend.repositories.TourImageRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.TourSpotRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_SPOT_NOT_FOUND;

@Service
public class TourSpotService {

    public final TourSpotRepository tourSpotRepository;
    public final TourRepository tourRepository;
    public final TourImageRepository tourImageRepository;

    @Autowired
    public TourSpotService(TourSpotRepository tourSpotRepository, TourRepository tourRepository, TourImageRepository tourImageRepository) {
        this.tourSpotRepository = tourSpotRepository;
        this.tourRepository = tourRepository;
        this.tourImageRepository = tourImageRepository;
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
//        newSpot.setTitle(form.title);
        //TODO:判断后添加的location范围
        newSpot.setLocation(form.location);
        TourSpot flushedTourSpot = tourSpotRepository.saveAndFlush(newSpot);
        TourImage newImage = new TourImage();
        newImage.setImageUrl(form.imageUrl);
        newImage.setTour(tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
        newImage.setTourSpot(flushedTourSpot);
        tourImageRepository.saveAndFlush(newImage);

        return flushedTourSpot;
    }

    public TourSpot updateTourSpot(UpdateTourSpotForm form) throws ResourceException {
        TourSpot existingSpot = getTourSpotById(form.tourSpotId);
        existingSpot.setTour(tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
//        existingSpot.setTitle(form.title);
//        existingSpot.setImageUrl(form.imageUrl);
        existingSpot.setLocation(form.location);
        return tourSpotRepository.saveAndFlush(existingSpot);
    }

    public void deleteTourSpot(Long tourSpotId) throws ResourceException {
        tourSpotRepository.delete(getTourSpotById(tourSpotId));
    }

    @Getter
    public static class CreateTourSpotForm {
        String title;
        //        String imageUrl;
        String imageUrl;
        String location;
        Long tourId;
    }

    @Getter
    public static class UpdateTourSpotForm extends CreateTourSpotForm {
        Long tourSpotId;
    }
}
