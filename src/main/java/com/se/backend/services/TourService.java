package com.se.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.*;
import com.se.backend.repositories.*;
import com.se.backend.utils.GpxUtil;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.se.backend.config.GlobalConfig.getStaticUrl;
import static com.se.backend.exceptions.AuthException.ErrorType.TOKEN_EXPIRED;
import static com.se.backend.exceptions.ResourceException.ErrorType.*;
import static com.se.backend.utils.FileUtil.saveFileToLocal;
import static com.se.backend.utils.FileUtil.stringToInputStream;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final TourCollectionRepository tourCollectionRepository;
    private final PONRepository ponRepository;

    private final GroupCollectionRepository groupCollectionRepository;


    private final UserRepository userRepository;


    @Autowired
    public TourService(TourRepository tourRepository, TourCollectionRepository tourCollectionRepository, UserRepository userRepository, PONRepository ponRepository, GroupCollectionRepository groupCollectionRepository) {
        this.tourRepository = tourRepository;
        this.tourCollectionRepository = tourCollectionRepository;
        this.ponRepository = ponRepository;
        this.userRepository = userRepository;
        this.groupCollectionRepository = groupCollectionRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }


    public Tour getTourById(Long tourId) throws ResourceException {
        return tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
    }

    public Tour createTour(User user, CreateTourForm form) throws ResourceException, AuthException, IOException {
        Tour newTour = new Tour();
        newTour.setType(form.type);
        newTour.setCreateTime(TimeUtil.getCurrentTimeString());
        newTour.setUser(user);
        newTour.setStartLocation(form.startLocation);
        newTour.setEndLocation(form.endLocation);
        newTour.setTitle(form.title);
        newTour.setState(Tour.TourState.UNFINISHED);
        newTour.setStatus(Tour.TourStatus.ONLINE);

        if (Objects.nonNull(form.tourCollectionId)) {
            TourCollection existingTourCollection = tourCollectionRepository.findById(form.tourCollectionId).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND));
            if (existingTourCollection.getUser().getId().equals(user.getId())) {
                newTour.setTourCollection(existingTourCollection);
            } else {
                throw new AuthException(TOKEN_EXPIRED);
            }
        } else {
            // TODO: Form validation exception
        }
        //Add tour to group collection
        if (Objects.nonNull(form.groupCollectionId) && form.groupCollectionId != -1) {
            GroupCollection existingGroupCollection = groupCollectionRepository.findById(form.groupCollectionId).orElseThrow(() -> new ResourceException(GROUP_COLLECTION_NOT_FOUND));
            newTour.setGroupCollection(existingGroupCollection);
        }

        newTour.setMapUrl("temp");
        newTour.setDataUrl("temp");
        newTour.setCompleteUrl("temp");
        Tour flushedTour = tourRepository.saveAndFlush(newTour);
        String tourDirectoryPath = "/tour/" + flushedTour.getId();
        // Clear the directory before adding new files
        File tourDirectory = new File(tourDirectoryPath);
        FileUtils.cleanDirectory(tourDirectory); // Requires Apache Commons IO

        flushedTour.setMapUrl(getStaticUrl(tourDirectoryPath + "/map_screenshot.jpg"));

        List<PON> attachedPONs = new ArrayList<>();
        for (PON pon : form.pons) {
            PON newPon = new PON();
            newPon.setTour(flushedTour);
            newPon.setName(pon.getName());
            newPon.setLocation(pon.getLocation());
            newPon.setSequence(pon.getSequence());
            attachedPONs.add(ponRepository.save(newPon)); // Save each PON
        }

        flushedTour.setPons(attachedPONs);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonContent = objectMapper.writeValueAsString(form);
            String relativePath = tourDirectoryPath + "/map.json";
            saveFileToLocal(stringToInputStream(jsonContent), relativePath);
            flushedTour.setDataUrl(getStaticUrl(relativePath));
        } catch (IOException e) {
            System.err.println("Error writing JSON to file: " + e.getMessage());
            // Handle the error according to your application's requirements
        }

        GpxUtil.JSONtoGPXFile(form, tourDirectoryPath + "/map.gpx");
//        String relativePath = "/tour/" + flushedTour.getId() + "/map.json";
//        GpxUtil.NavigationData data = GpxUtil.JsonGpxConverter.parseJsonToNavigationData((getStaticUrl(relativePath)));
        return tourRepository.saveAndFlush(flushedTour);
    }

//    public Tour uploadGPXCreateTour (User user, MultipartFile file, uploadGpxForm form) throws Exception{
//        Tour newTour = new Tour();
//        newTour.setType(form.type);
//        newTour.setCreateTime(TimeUtil.getCurrentTimeString());
//        newTour.setUser(user);
//        newTour.setTitle(form.title);
//        newTour.setState(Tour.TourState.UNFINISHED);
//        newTour.setStatus(Tour.TourStatus.AWAIT_APPROVAL);
//        if (Objects.nonNull(form.tourCollectionId)) {
//            TourCollection existingTourCollection = tourCollectionRepository.findById(form.tourCollectionId).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND));
//            if (existingTourCollection.getUser().getId().equals(user.getId())) {
//                newTour.setTourCollection(existingTourCollection);
//            } else {
//                throw new AuthException(TOKEN_EXPIRED);
//            }
//        } else {
//            // TODO: Form validation exception
//        }
//        newTour.setMapUrl("temp");
//        newTour.setDataUrl("temp");
//        newTour.setCompleteUrl("temp");
//        Tour flushedTour = tourRepository.saveAndFlush(newTour);
//        flushedTour.setMapUrl(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));
//        String relativePath = "/tour/" + flushedTour.getId() + "/map.gpx";
//        saveFileToLocal(file.getInputStream(), relativePath);
//        Path path = Paths.get(relativePath);
//
//        CreateTourForm formgpx = GpxUtil.GpxToNavigationDataConverter.parseGpxToNavigationData(path.toString());
//        newTour.setStartLocation(formgpx.result.getOrigin().toString());
//        newTour.setEndLocation(formgpx.result.getDestination().toString());
//        List<PON> attachedPONs = new ArrayList<>();
//        for (PON pon : formgpx.getPons()) {
//            PON newPon = new PON();
//            newPon.setTour(flushedTour);
//            newPon.setName(pon.getName());
//            newPon.setLocation(pon.getLocation());
//            newPon.setSequence(pon.getSequence());
//            attachedPONs.add(ponRepository.save(newPon)); // Save each PON
//        }
//        flushedTour.setPons(attachedPONs);
//        formgpx.setTourId(flushedTour.getId());
//        formgpx.setTitle(form.title);
//        formgpx.setType(form.type);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String jsonContent = objectMapper.writeValueAsString(formgpx);
//            String relativePath1 = "/tour/" + flushedTour.getId() + "/map.json";
//            saveFileToLocal(stringToInputStream(jsonContent), relativePath1);
//            flushedTour.setDataUrl(getStaticUrl(relativePath1));
//        } catch (IOException e) {
//            System.err.println("Error writing JSON to file: " + e.getMessage());
//            // Handle the error according to your application's requirements
//        }
//        return tourRepository.saveAndFlush(flushedTour);
//    }

    public Tour updateTour(UpdateTourForm updatedTourInfo) throws ResourceException {
        Tour existingTour = getTourById(updatedTourInfo.id);
        existingTour.setStartLocation(updatedTourInfo.getStartLocation());
        existingTour.setEndLocation(updatedTourInfo.getEndLocation());
        existingTour.setType(updatedTourInfo.getType());
//        List<PON> attachedPONs = existingTour.getPons();
//        attachedPONs.clear();
//        for (PON pon : updatedTourInfo.getPons()) {
//            PON newPon = new PON();
//            newPon.setTour(existingTour);
//            newPon.setName(pon.getName());
//            newPon.setLocation(pon.getLocation());
//            newPon.setSequence(pon.getSequence());
//            attachedPONs.add(ponRepository.save(newPon)); // Save each PON
//        }
//        existingTour.setPons(attachedPONs);
        existingTour.setTitle(updatedTourInfo.getTitle());
        existingTour.setState(updatedTourInfo.getState());
        existingTour.setStatus(updatedTourInfo.status);
        existingTour.setTourCollection(tourCollectionRepository.findById(updatedTourInfo.getTourCollectionId()).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND)));
        return tourRepository.save(existingTour);
    }


    public Tour completeTour(SaveTourForm saveTourForm) throws ResourceException {
        Tour existingTour = getTourById(saveTourForm.getTourId());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonContent = objectMapper.writeValueAsString(saveTourForm);
            String relativePath = "/tour/" + existingTour.getId() + "/complete.json";
            saveFileToLocal(stringToInputStream(jsonContent), relativePath);
            existingTour.setCompleteUrl(getStaticUrl(relativePath));
            if (saveTourForm.isComplete) {
                existingTour.setState(Tour.TourState.FINISHED);
            } else {
                existingTour.setState(Tour.TourState.ONGOING);
            }
            //TODO:record data to database
            TourRecordData saveTourRecordData = new TourRecordData();

            saveTourRecordData.setAvgSpeed(saveTourForm.getRecordData().getAvgSpeed());
            saveTourRecordData.setTimeTaken(saveTourForm.getRecordData().getTimeTaken());
            saveTourRecordData.setTotalDistance(saveTourForm.getRecordData().getTotalDistance());
            saveTourRecordData.setTimeInMotion(saveTourForm.getRecordData().getTimeInMotion());
            saveTourRecordData.setCalorie(saveTourForm.getRecordData().getCalorie());

            existingTour.setTourRecordData(saveTourRecordData);
            tourRepository.saveAndFlush(existingTour);


        } catch (IOException e) {
            System.err.println("Error writing Complete JSON to file: " + e.getMessage());
        }
        return existingTour;
    }


    public List<Tour> getToursByUser(User user) {
        return tourRepository.findAllByUser(user);
    }

    public List<ContentDataRecord> getWeeklyTour() {
        List<Tour> allTours = tourRepository.findAll();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        // Convert createTime from String to LocalDate and filter the last 7 days
        Map<LocalDate, Long> dateCounts = allTours.stream().map(tour -> LocalDate.parse(tour.getCreateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).filter(date -> date.isAfter(sevenDaysAgo)).collect(Collectors.groupingBy(date -> date, Collectors.counting()));
        // Convert map to list of ContentDataRecord
        List<ContentDataRecord> records = new ArrayList<>();
        dateCounts.forEach((date, count) -> records.add(new ContentDataRecord(date.toString(), count)));
        // Sort records by date
        records.sort(Comparator.comparing(ContentDataRecord::getDate));
        return records;
    }

    @Transactional
    public void deleteTour(Long tourId) throws ResourceException {
        tourRepository.delete(tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
    }

    @Transactional
    public Tour likeTour(User user, Long tourId) throws ResourceException {
        Set<Tour> tourLikes = user.getTourLikes();
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        for (Tour t : tourLikes) {
            if (t.getId().equals(tourId)) {
                throw new ResourceException(TOUR_LIKE_EXISTS);
            }
        }
        tour.getLikedBy().add(user);
        return tourRepository.saveAndFlush(tour);
    }

    @Transactional
    public Tour starTour(User user, Long tourId) throws ResourceException {
        Set<Tour> tourStars = user.getTourStars();
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        for (Tour t : tourStars) {
            if (t.getId().equals(tourId)) {
                throw new ResourceException(TOUR_LIKE_EXISTS);
            }
        }
        tour.getStarredBy().add(user);
        return tourRepository.saveAndFlush(tour);
    }

    @Transactional
    public Tour cancelLikeTour(User user, Long tourId) throws ResourceException {
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        tour.getLikedBy().removeIf(u -> u.getId().equals(user.getId()));
        return tourRepository.saveAndFlush(tour);
    }

    @Transactional
    public Tour cancelStarTour(User user, Long tourId) throws ResourceException {
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
        tour.getStarredBy().removeIf(u -> u.getId().equals(user.getId()));
        return tourRepository.saveAndFlush(tour);
    }

    @Getter
    @Setter
    public static class CreateTourForm {
        String startLocation;
        String endLocation;
        Tour.TourType type;

        // FIXME : create PON structure
        List<PON> pons;
        Long tourCollectionId;
        Long groupCollectionId;
        String title;
        GpxUtil.NavigationData result;
    }

    @Getter
    public static class UpdateTourForm extends CreateTourForm {
        Long id;
        Tour.TourStatus status;
        Tour.TourState state;
    }

    @Getter
    public static class uploadGpxForm {
        //        Long tourId;
//        String gpxUrl;
        Tour.TourType type;
        Long tourCollectionId;
        String title;
    }

    @Getter
    public static class SaveTourForm {
        Long tourId;
        Boolean isComplete;
        CompletedTourData recordData;
        private List<RecordDataInstant> trackList;
    }

    @Getter
    public static class CompletedTourData {
        private Double avgSpeed;
        private Double totalDistance;
        private Double timeInMotion;
        private Double timeTaken;
        private Double calorie;
    }

    @Getter
    public static class RecordDataInstant {
        private List<Double> location;
        private Double speed;
        private Double altitude;
        private String time;
    }

    @Getter
    public static class ContentDataRecord {
        String date;
        Long number;

        public ContentDataRecord(String date, Long number) {
            this.date = date;
            this.number = number;
        }
    }
}
