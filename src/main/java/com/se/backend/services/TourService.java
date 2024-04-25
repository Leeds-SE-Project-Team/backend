package com.se.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.*;
import com.se.backend.projection.TourDTO;
import com.se.backend.projection.UserDTO;
import com.se.backend.repositories.*;
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

    private final TourLikeRepository tourLikeRepository;
    private final TourStarRepository tourStarRepository;

    private final UserRepository userRepository;


    @Autowired
    public TourService(TourRepository tourRepository, TourCollectionRepository tourCollectionRepository, UserRepository userRepository, PONRepository ponRepository, TourLikeRepository tourLikeRepository, TourStarRepository tourStarRepository) {
        this.tourRepository = tourRepository;
        this.tourCollectionRepository = tourCollectionRepository;
        this.ponRepository = ponRepository;
        this.userRepository = userRepository;
        this.tourLikeRepository = tourLikeRepository;
        this.tourStarRepository = tourStarRepository;
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
//        String relativePath = "/tour/" + flushedTour.getId() + "/map.json";
//        GpxUtil.NavigationData data = GpxUtil.JsonGpxConverter.parseJsonToNavigationData((getStaticUrl(relativePath)));
        return tourRepository.saveAndFlush(flushedTour);
    }

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
            tourRepository.saveAndFlush(existingTour);

        } catch (IOException e) {
            System.err.println("Error writing Complete JSON to file: " + e.getMessage());
        }
        return existingTour;
    }

    //add to exist json
//    private void appendCompletedTourDataToFile(Long tourId, List<CompletedTourData> completedTourDataList) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        File file = new File("/path/to/your/directory/tour_" + tourId + ".json");
//        GpxUtil.NavigationData tourJson = objectMapper.readValue(file, GpxUtil.NavigationData.class); // 读取原始 JSON 文件内容
//        // 将完成的 Tour 数据追加到 JSON 结构中的 saveTour 字段中
//        if (tourJson.getSaveTour() == null) {
//            tourJson.setSaveTour(completedTourDataList);
//        } else {
//            tourJson.getSaveTour().addAll(completedTourDataList);
//        }
//
//        // 将更新后的 JSON 结构写入文件中
//        objectMapper.writeValue(file, tourJson);
//    }

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

    public void likeTour(Long userId, Long tourId) throws ResourceException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));

        // 检查点赞是否已存在
        if (!tourLikeRepository.findByUserIdAndTourId(userId, tourId).isEmpty()) {
            throw new ResourceException(TOUR_LIKE_EXISTS);
        }
        TourLike NewTourLike = new TourLike();
        NewTourLike.setUser(user);
        NewTourLike.setTour(tour);
        NewTourLike.setCreateTime(TimeUtil.getCurrentTimeString());
        //TODO：return DTO
        tourLikeRepository.saveAndFlush(NewTourLike);
    }

    public void starTour(Long userId, Long tourId) throws ResourceException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceException(USER_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new ResourceException(TOUR_NOT_FOUND));

        if (!tourStarRepository.findByUserIdAndTourId(userId, tourId).isEmpty()) {
            throw new ResourceException(TOUR_STAR_EXISTS);
        }
        TourStar NewTourStar = new TourStar();
        NewTourStar.setUser(user);
        NewTourStar.setTour(tour);
        NewTourStar.setCreatetTime(TimeUtil.getCurrentTimeString());

        tourStarRepository.saveAndFlush(NewTourStar);
    }
    @Transactional
    public void cancelLikeTour(Long userId, Long tourId) throws ResourceException {
        Optional<TourLike> like = tourLikeRepository.findByUserIdAndTourId(userId, tourId);
        if (like.isEmpty()) {
            throw new ResourceException(TOUR_LIKE_NOT_FOUND);
        }
        tourLikeRepository.delete(like.get());
        System.out.println(like);
    }

    public void cancelStarTour(Long userId, Long tourId) throws ResourceException {
        List<TourStar> stars = tourStarRepository.findByUserIdAndTourId(userId, tourId);
        if (stars.isEmpty()) {
            throw new ResourceException(TOUR_STAR_NOT_FOUND);
        }
        tourStarRepository.deleteAll(stars); // Assuming there could be multiple stars which is usually not the case
    }

    public List<TourDTO> getAllLikedToursByUserId(Long userId) throws ResourceException {
        if (!tourRepository.existsById(userId)) {
            throw new ResourceException(USER_NOT_FOUND);
        }
        List<TourLike> likes = tourLikeRepository.findAllByUserId(userId);
        return TourDTO.toListDTO(likes.stream().map(TourLike::getTour).collect(Collectors.toList()));
    }

    public List<TourDTO> getAllStarredToursByUserId(Long userId) throws ResourceException {
        if (!tourRepository.existsById(userId)) {
            throw new ResourceException(USER_NOT_FOUND);
        }
        List<TourStar> stars = tourStarRepository.findAllByUserId(userId);
        return TourDTO.toListDTO(stars.stream().map(TourStar::getTour).collect(Collectors.toList()));
    }

    public List<UserDTO> getAllUsersByLikedTourId(Long tourId) throws ResourceException {
        if (!tourRepository.existsById(tourId)) {
            throw new ResourceException(TOUR_NOT_FOUND);
        }
        List<TourLike> likes = tourLikeRepository.findAllByTourId(tourId);
        return UserDTO.toListDTO(likes.stream().map(TourLike::getUser).distinct().collect(Collectors.toList()));
    }

    public List<UserDTO> getAllUsersByStarredTourId(Long tourId) throws ResourceException {
        if (!tourRepository.existsById(tourId)) {
            throw new ResourceException(TOUR_NOT_FOUND);
        }
        List<TourStar> stars = tourStarRepository.findAllByTourId(tourId);
        return UserDTO.toListDTO(stars.stream().map(TourStar::getUser).distinct().collect(Collectors.toList()));
    }

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
        Long tourId;
        String gpxUrl;
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
