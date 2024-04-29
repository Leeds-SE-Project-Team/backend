package com.se.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.*;
import com.se.backend.projection.TourDTO;
import com.se.backend.repositories.PONRepository;
import com.se.backend.repositories.TourCollectionRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.GpxUtil;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    private final UserRepository userRepository;


    @Autowired
    public TourService(TourRepository tourRepository, TourCollectionRepository tourCollectionRepository, UserRepository userRepository, PONRepository ponRepository) {
        this.tourRepository = tourRepository;
        this.tourCollectionRepository = tourCollectionRepository;
        this.ponRepository = ponRepository;
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
        newTour.setMapUrl("temp");
        newTour.setDataUrl("temp");
        newTour.setCompleteUrl("temp");
        Tour flushedTour = tourRepository.saveAndFlush(newTour);
        flushedTour.setMapUrl(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));

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
            String relativePath = "/tour/" + flushedTour.getId() + "/map.json";
            saveFileToLocal(stringToInputStream(jsonContent), relativePath);
            flushedTour.setDataUrl(getStaticUrl(relativePath));
//            Files.write(Paths.get("/path/to/your/directory/tour_" + flushedTour.getId() + ".json"), jsonContent.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing JSON to file: " + e.getMessage());
            // Handle the error according to your application's requirements
        }

        GpxUtil.JSONtoGPXFile(form, "/tour/" + flushedTour.getId() + "/map.gpx");
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
//
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
//        CreateTourForm form = GpxUtil.GpxToNavigationDataConverter.parseGpxToNavigationData(path.toString());
//        newTour.setStartLocation(form.result.getOrigin().toString());
//        newTour.setEndLocation(navigationData.getDestination().toString());
//        List<PON> attachedPONs = new ArrayList<>();
//        for (GpxUtil.NavigationData.WayPoint wayPoint : navigationData.getWayPoints()) {
//            PON newPon = new PON();
//            newPon.setTour(flushedTour);
//            newPon.setName(wayPoint.getName());
//            newPon.setLocation(wayPoint.getLocation().toString());
//            newPon.setSequence(wayPoint.getSequence());
//            attachedPONs.add(ponRepository.save(newPon)); // Save each PON
//        }
//        flushedTour.setPons(attachedPONs);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String jsonContent = objectMapper.writeValueAsString(navigationData);
//            String relativePath1 = "/tour/" + flushedTour.getId() + "/map.json";
//            saveFileToLocal(stringToInputStream(jsonContent), relativePath1);
//            flushedTour.setDataUrl(getStaticUrl(relativePath1));
//        } catch (IOException e) {
//            System.err.println("Error writing JSON to file: " + e.getMessage());
//            // Handle the error according to your application's requirements
//        }
//
//        return tourRepository.saveAndFlush(flushedTour);
//    }

//    public Tour uploadGPXCreateTour (User user,uploadGpxForm from) throws ResourceException{
    //获取前端传过来的文件
    //读取GPX文件数据转化到GpxUtil.NavigationData类中
    //创建new tour 将form中的数据填充数据库
    //通过 ObjectMapper objectMapper = new ObjectMapper();将其写入带有Tourid的json中
    //将gpx写入带有Tourid的gpx文件中
    //删除原先的文件
    //返回前端Tour的数据

    @Transactional
    public void deleteTour(Long tourId) throws ResourceException {
        tourRepository.delete(tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND)));
    }

    //    public Tour uploadGPX (uploadGpxForm from) throws ResourceException{
//

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

    public Tour likeTour(User user, Long tourId) throws ResourceException {


        Set<Tour> tourLiked = user.getTourLikes();
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));

//        if (tourLiked.contains(tour)) {
//            throw new ResourceException(TOUR_LIKE_EXISTS);
//        }
        // 检查点赞是否已存在
        for (Tour t : tourLiked) {
            if (t.getId().equals(tourId)) {
                //  已经存在
                throw new ResourceException(TOUR_LIKE_EXISTS);
            }
        }
//        tourLiked.add(tour);
        tour.getLikedBy().add(user);
//        tourLiked.add(tour);
//        user.setTourLikes(tourLiked);
//        userRepository.save(user);

        return tourRepository.saveAndFlush(tour);
    }

    public Tour starTour(User user, Long tourId) throws ResourceException {
        Set<Tour> tourStarred = user.getTourStars();
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));


        for (Tour t : tourStarred) {
            if (t.getId().equals(tourId)) {
                //  已经存在
                throw new ResourceException(TOUR_STAR_EXISTS);
            }
        }
        tourStarred.add(tour);
        user.setTourStars(tourStarred);
        userRepository.save(user);
        return tour;
    }

    @Transactional
    public Tour cancelLikeTour(User user, Long tourId) throws ResourceException {
        Set<Tour> tourLiked = user.getTourLikes();
        tourLiked.removeIf(t -> t.getId().equals(tourId));
        user.setTourLikes(tourLiked);
        userRepository.save(user);
        return tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
//        throw new ResourceException(TOUR_LIKE_NOT_FOUND);
    }

    @Transactional

    public Tour cancelStarTour(User user, Long tourId) throws ResourceException {
        Set<Tour> tourStarred = user.getTourStars();
        tourStarred.removeIf(t -> t.getId().equals(tourId));
        user.setTourStars(tourStarred);
        userRepository.save(user);
        return tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));
    }
//
//    public List<TourDTO> getAllLikedToursByUserId(Long userId) throws ResourceException {
//        if (!tourRepository.existsById(userId)) {
//            throw new ResourceException(USER_NOT_FOUND);
//        }
//        List<TourLike> likes = tourLikeRepository.findAllByUserId(userId);
//        return TourDTO.toListDTO(likes.stream().map(TourLike::getTour).collect(Collectors.toList()));
//    }

//    public List<TourDTO> getAllStarredToursByUserId(Long userId) throws ResourceException {
//        if (!tourRepository.existsById(userId)) {
//            throw new ResourceException(USER_NOT_FOUND);
//        }
//        List<TourStar> stars = tourStarRepository.findAllByUserId(userId);
//        return TourDTO.toListDTO(stars.stream().map(TourStar::getTour).collect(Collectors.toList()));
//    }

//    public List<UserDTO> getAllUsersByLikedTourId(Long tourId) throws ResourceException {
////        if (!tourRepository.existsById(tourId)) {
////            throw new ResourceException(TOUR_NOT_FOUND);
////        }
////        List<TourLike> likes = tourLikeRepository.findAllByTourId(tourId);
////        return UserDTO.toListDTO(likes.stream().map(TourLike::getUser).distinct().collect(Collectors.toList()));
//    }

//    public List<UserDTO> getAllUsersByStarredTourId(Long tourId) throws ResourceException {
//        if (!tourRepository.existsById(tourId)) {
//            throw new ResourceException(TOUR_NOT_FOUND);
//        }
//        List<TourStar> stars = tourStarRepository.findAllByTourId(tourId);
//        return UserDTO.toListDTO(stars.stream().map(TourStar::getUser).distinct().collect(Collectors.toList()));
//    }

    @Getter
    public static class CreateTourForm {
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
