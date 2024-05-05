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
        newHighlight.setDescription(form.description);

        Tour existingTour = tourRepository.findById(form.tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        existingTour.getHighlights().add(newHighlight);
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
        newImage.setTour(existingTour); // TODO: tour not exist

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
        existingHighlight.setDescription(form.description);

        return tourHighlightRepository.saveAndFlush(existingHighlight);
    }

    public void deleteTourHighlight(Long tourHighlightId) throws ResourceException {
        TourHighlight highlightToDelete = getTourHighlightById(tourHighlightId);

        // Unlink the highlight from any tours
        List<Tour> associatedTours = highlightToDelete.getTours();
        if (associatedTours != null) {
            for (Tour tour : associatedTours) {
                tour.getHighlights().remove(highlightToDelete);
                tourRepository.save(tour); // Update each tour after removing the highlight
            }
        }

        // Delete associated images if needed
        List<TourImage> associatedImages = tourImageRepository.findByTourHighlight(highlightToDelete);
        if (associatedImages != null) {
            tourImageRepository.deleteAll(associatedImages); // Or handle them according to your policy
        }

        // Now it is safe to delete the highlight
        tourHighlightRepository.delete(highlightToDelete);
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
        String description;
//        List<String> imageURLs;
    }

    @Getter
    public static class UpdateTourHighlightForm extends TourHighlightService.CreateTourHighlightForm {
        Long tourHighlightId;
    }

}
