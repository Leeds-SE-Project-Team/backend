package com.se.backend.services;

import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Tour;
import com.se.backend.models.TourHighlight;
import com.se.backend.models.TourImage;
import com.se.backend.repositories.TourHighlightRepository;
import com.se.backend.repositories.TourImageRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.utils.FileUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_Highlight_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;


@Service
public class TourHighlightService {
    public final TourRepository tourRepository;
    public final TourHighlightRepository tourHighlightRepository;

    public final TourImageRepository tourImageRepository;

    @Autowired
    public TourHighlightService(TourHighlightRepository tourHighlightRepository, TourRepository tourRepository, TourImageRepository tourImageRepository) {
        this.tourHighlightRepository = tourHighlightRepository;
        this.tourRepository = tourRepository;
        this.tourImageRepository = tourImageRepository;
    }

    public List<TourHighlight> getAllTourHighlights() {
        return tourHighlightRepository.findAll();
    }

    public TourHighlight getTourHighlightById(Long tourHighlightId) throws ResourceException {
        return tourHighlightRepository.findById(tourHighlightId).orElseThrow(() -> new ResourceException(TOUR_Highlight_NOT_FOUND));
    }

    public void createTourHighlight(TourHighlightService.CreateTourHighlightForm form) throws ResourceException {
        TourHighlight newHighlight = new TourHighlight();
        newHighlight.setTitle(form.title);
        newHighlight.setLocation(form.location);

        Tour exsitingTour = tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        exsitingTour.getHighlights().add(newHighlight);
//        tourHighlightRepository.saveAndFlush(newHighlight)
//        List<Tour> highlightTours = newHighlight.getTours();
//        if (Objects.nonNull(highlightTours)) {
//            if (Objects.nonNull(exsitingTour.getHighlights())) {
//                exsitingTour.getHighlights().add(newHighlight);
//            } else {
//                exsitingTour.setHighlights(new ArrayList<>(List.of(newHighlight)));
//            }
//            highlightTours.add(tourRepository.saveAndFlush(exsitingTour));
//        } else {
//            newHighlight.setTours(new ArrayList<>(List.of(exsitingTour)));
//        }

        TourImage newImage = new TourImage();
        newImage.setImageUrl(form.imageUrl);
        newImage.setTour(exsitingTour); // TODO: tour not exist

        TourHighlight flushedHighlight = tourHighlightRepository.saveAndFlush(newHighlight);
        newImage.setTourHighlight(flushedHighlight);
        tourImageRepository.saveAndFlush(newImage);
    }

    public TourHighlight updateTourHighlight(UpdateTourHighlightForm form) throws ResourceException {
        TourHighlight existingHighlight = getTourHighlightById(form.tourHighlightId);
//        existingHighlight.setTours(tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
        existingHighlight.setTitle(form.title);
//        existingHighlight.setImageUrl(form.imageUrl);
        existingHighlight.setLocation(form.location);

        return tourHighlightRepository.saveAndFlush(existingHighlight);
    }

    public void deleteTourHighlight(Long tourHighlightId) throws ResourceException {
        tourHighlightRepository.delete(getTourHighlightById(tourHighlightId));
    }

    public void uploadAndFlushImage(TourHighlight tourHighlight, Tour tour, String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        if (FileUtil.downloadFromUrl(imageUrl, filename, "./")) {
            TourImage newTourImage = new TourImage();
            newTourImage.setTourHighlight(tourHighlight);
            newTourImage.setImageUrl(imageUrl);
            newTourImage.setTour(tour);
            tourHighlight.getTourImages().add(tourImageRepository.saveAndFlush(newTourImage));
            tourHighlightRepository.saveAndFlush(tourHighlight);
        }
    }

    @Getter
    public static class CreateTourHighlightForm {
        String title;
        //        String imageUrl;
        String location;
        Long tourId;
        String imageUrl;
//        List<String> imageURLs;
    }

    @Getter
    public static class UpdateTourHighlightForm extends TourHighlightService.CreateTourHighlightForm {
        Long tourHighlightId;
    }

}
