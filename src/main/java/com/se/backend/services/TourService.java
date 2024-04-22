package com.se.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.PON;
import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.repositories.TourCollectionRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.GpxUtil;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.se.backend.config.GlobalConfig.getStaticUrl;
import static com.se.backend.exceptions.AuthException.ErrorType.TOKEN_EXPIRED;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_COLLECTION_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;
import static com.se.backend.utils.FileUtil.saveFileToLocal;
import static com.se.backend.utils.FileUtil.stringToInputStream;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final TourCollectionRepository tourCollectionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TourService(TourRepository tourRepository, TourCollectionRepository tourCollectionRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.tourCollectionRepository = tourCollectionRepository;
        this.userRepository = userRepository;
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
//        newTour.setPons(form.pons);
        newTour.setStartLocation(form.startLocation);
        newTour.setEndLocation(form.endLocation);
        newTour.setTitle(form.title);
        newTour.setState(Tour.TourState.UNFINISHED);
        newTour.setStatus(Tour.TourStatus.AWAIT_APPROVAL);

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
        newTour.setMapUrl("temp");
        newTour.setDataUrl("temp");
        Tour flushedTour = tourRepository.saveAndFlush(newTour);
//        System.out.println(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));
        flushedTour.setMapUrl(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));
        flushedTour.setPons(form.pons);//假设当Tour执行完Save后PON可以进行绑定

        //循环将Pon绑定并创建数据
//        PON newPon = new PON();
//        newPon.setTour(flushedTour);
//        newPon.setName(form.pons.name);
//        newPon.setName(form.pons.location);
//        newPon.setName(form.pons.sequence);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonContent = objectMapper.writeValueAsString(form.result);
            String relativePath = "/tour/" + flushedTour.getId() + "/map.json";
            saveFileToLocal(stringToInputStream(jsonContent), relativePath);
            newTour.setDataUrl(getStaticUrl(relativePath));
//            Files.write(Paths.get("/path/to/your/directory/tour_" + flushedTour.getId() + ".json"), jsonContent.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing JSON to file: " + e.getMessage());
            // Handle the error according to your application's requirements
        }

        GpxUtil.JSONtoGPXFile(form.result, "/tour/" + flushedTour.getId() + "/map.gpx");

        return tourRepository.saveAndFlush(flushedTour);
    }

    public Tour updateTour(UpdateTourForm updatedTourInfo) throws ResourceException {
        Tour existingTour = getTourById(updatedTourInfo.tourId);
        existingTour.setStartLocation(updatedTourInfo.getStartLocation());
        existingTour.setEndLocation(updatedTourInfo.getEndLocation());
        existingTour.setType(updatedTourInfo.getType());
        existingTour.setPons(updatedTourInfo.getPons());
        existingTour.setTitle(updatedTourInfo.getTitle());
        existingTour.setState(updatedTourInfo.getState());
        existingTour.setStatus(updatedTourInfo.status);
        existingTour.setTourCollection(tourCollectionRepository.findById(updatedTourInfo.getTourCollectionId()).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND)));
        return tourRepository.save(existingTour);
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

    @Getter
    public static class CreateTourForm {
        Long tourId;
        String startLocation;
        String endLocation;
        Tour.TourType type;

        // FIXME : create PON structure
        List<PON> pons;
        Long tourCollectionId;
        String title;
        GpxUtil.NavigationData result;
    }

    @Getter
    public static class UpdateTourForm extends CreateTourForm {
        Tour.TourStatus status;
        Tour.TourState state;
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
