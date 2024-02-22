package com.se.backend.services;


import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.repositories.UserRepository;
import com.se.backend.repositories.TourCollectionRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_COLLECTION_NOT_FOUND;

@Service
public class TourCollectionService {
    private final TourCollectionRepository tourCollectionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TourCollectionService(TourCollectionRepository tourCollectionRepository, UserRepository userRepository) {
        this.tourCollectionRepository = tourCollectionRepository;
        this.userRepository = userRepository;
    }

    public TourCollection getTourCollectionById(Long tourCollectionId) throws ResourceException {
        return tourCollectionRepository.findById(tourCollectionId).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND));
    }

    @Getter
    public static class CreateTourCollectionForm {
        String title;
        String coverUrl;
        String description;
        List<Tour> tours;
    }

    public List<TourCollection> getAllTourCollections() {
        return tourCollectionRepository.findAll();
    }

    public TourCollection createTourCollection(User user, TourCollectionService.CreateTourCollectionForm form) {

        TourCollection newTourCollection = new TourCollection();
        newTourCollection.setUser(user);
        newTourCollection.setTitle(form.title);

        newTourCollection.setCoverUrl(form.coverUrl);

        newTourCollection.setDescription(form.description);
        newTourCollection.setTours(form.tours);

        return tourCollectionRepository.saveAndFlush(newTourCollection);
    }


}
