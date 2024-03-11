package com.se.backend.services;


import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.repositories.TourCollectionRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_COLLECTION_NOT_FOUND;

@Service
public class TourCollectionService {
    private final TourCollectionRepository tourCollectionRepository;

    @Autowired
    public TourCollectionService(TourCollectionRepository tourCollectionRepository) {
        this.tourCollectionRepository = tourCollectionRepository;

    }

    public TourCollection getTourCollectionById(Long tourCollectionId) throws ResourceException {
        return tourCollectionRepository.findById(tourCollectionId).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND));
    }

    public List<TourCollection> getAllTourCollections() {
        return tourCollectionRepository.findAll();
    }
    public List<TourCollection> getTourCollectionByUser(User user) {
        return tourCollectionRepository.findAllByUser(user);
    }

    public TourCollection createTourCollection(User user, TourCollectionService.CreateTourCollectionForm form) {
        TourCollection newTourCollection = new TourCollection();
        newTourCollection.setUser(user);
        newTourCollection.setName(form.name);
        newTourCollection.setTitle(form.title);
        newTourCollection.setCoverUrl(form.coverUrl);
        newTourCollection.setDescription(form.description);
        return tourCollectionRepository.saveAndFlush(newTourCollection);
    }

    @Getter
    @AllArgsConstructor
    public static class CreateTourCollectionForm {
        String name;
        String title;
        String coverUrl;
        String description;
    }


}
