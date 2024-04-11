package com.se.backend.services;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.PON;
import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import com.se.backend.repositories.TourCollectionRepository;
import com.se.backend.repositories.TourRepository;
import com.se.backend.repositories.UserRepository;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.se.backend.config.GlobalConfig.getStaticUrl;
import static com.se.backend.exceptions.AuthException.ErrorType.TOKEN_EXPIRED;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_COLLECTION_NOT_FOUND;
import static com.se.backend.exceptions.ResourceException.ErrorType.TOUR_NOT_FOUND;

//GPX-JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;
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

    public Tour createTour(User user, CreateTourForm form) throws ResourceException, AuthException {
        Tour newTour = new Tour();
        newTour.setType(form.type);
        newTour.setCreateTime(TimeUtil.getCurrentTimeString());
        newTour.setUser(user);
        newTour.setPons(form.pons);
        newTour.setStartLocation(form.startLocation);
        newTour.setEndLocation(form.endLocation);
        newTour.setTitle(form.title);

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
        Tour flushedTour = tourRepository.saveAndFlush(newTour);
        System.out.println(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));
        flushedTour.setMapUrl(getStaticUrl("/tour/" + flushedTour.getId() + "/map_screenshot.jpg"));
        return tourRepository.saveAndFlush(flushedTour);
    }

    public Tour updateTour(Long id, UpdateTourForm updatedTourInfo) throws ResourceException {
        Tour existingTour = getTourById(id);
        existingTour.setStartLocation(updatedTourInfo.getStartLocation());
        existingTour.setEndLocation(updatedTourInfo.getEndLocation());
        existingTour.setType(updatedTourInfo.getType());
        existingTour.setPons(updatedTourInfo.getPons());
        existingTour.setTitle(updatedTourInfo.getTitle());
        existingTour.setTourCollection(tourCollectionRepository.findById(updatedTourInfo.getTourCollectionId()).orElseThrow(() -> new ResourceException(TOUR_COLLECTION_NOT_FOUND)));
        return tourRepository.save(existingTour);
    }

    @Getter
    public static class CreateTourForm {
        String startLocation;
        String endLocation;
        Tour.TourType type;
        List<PON> pons;
        Long tourCollectionId;
        String title;
    }


    @Getter
    public static class UpdateTourForm extends CreateTourForm {
    }
    @Getter
    public class NavigationData {
        private String info;
        private List<Double> origin;
        private List<Double> destination;
        private int count;
        private List<Route> routes;
        private Location start;
        private Location end;
        // Getters and setters
    }
    @Getter
    public class Route {
        private int distance;
        private int time;
        private List<Step> steps;
        // Getters and setters
    }
    @Getter
    public class Step {
        private List<Double> start_location;
        private List<Double> end_location;
        private String instruction;
        private String road;
        private String orientation;
        private int distance;
        private int time;
        private List<List<Double>> path;
        private String action;
        private String assistant_action;
        // Getters and setters
    }
    @Getter
    public class Location {
        private List<Double> location;
        private String name;
        private String type;
        // Getters and setters
    }

    public class JsonGpxConverter {

        public static NavigationData parseJsonToNavigationData(String filePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(Paths.get(filePath).toFile(), NavigationData.class);
        }
    }

    public class FileUtils {

        public static void writeStringToFile(String content, String filePath) throws IOException {
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(content);
            }
        }
    }
    public class GpxSerializer {

        public static String toGpx(NavigationData navigationData) {
            StringBuilder gpxBuilder = new StringBuilder();
            gpxBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            gpxBuilder.append("<gpx version=\"1.1\" creator=\"www.Walcraft.com\">\n");

            // Adding metadata tag if needed based on navigationData properties
            // This is an example, adjust according to your actual metadata requirements
            gpxBuilder.append("<metadata>\n");
            gpxBuilder.append(String.format("<name>%s</name>\n", "Your Route Name")); // Example, replace with actual data if available
            gpxBuilder.append(String.format("<email>%s</email>\n", "Your Route email")); // Example, replace with actual data if available


            gpxBuilder.append("</metadata>\n");

            for (Route route : navigationData.getRoutes()) {
                gpxBuilder.append("<trk>\n");
                gpxBuilder.append("<name>Your Track Name</name>\n"); // Example, replace with actual data
                gpxBuilder.append("<desc></desc>\n"); // Description, empty as per requirement
                gpxBuilder.append("<type></type>\n"); // Type, empty as per requirement
                gpxBuilder.append("<trkseg>\n");
                for (Step step : route.getSteps()) {
                    gpxBuilder.append("<trkpt>\n");
                    for (List<Double> point : step.getPath()) {
                        gpxBuilder.append(formatWaypoint("wpt", point, ""));
//                        gpxBuilder.append(String.format("<wpt lat=\"%f\" lon=\"%f\">\n", point.get(1), point.get(0)));
//                        gpxBuilder.append("<ele></ele>\n"); // Elevation, empty as per requirement
//                        gpxBuilder.append("<time></time>\n"); // Time, empty as per requirement
//                        gpxBuilder.append("<desc></desc>\n"); // Description, empty as per requirement
//                        gpxBuilder.append("<type></type>\n"); // Type, empty as per requirement
//                        gpxBuilder.append("<extensions></extensions>\n"); // Extensions, empty as per requirement
//                        gpxBuilder.append("</wpt>\n");
                    }
                    gpxBuilder.append("<extensions>\n");
                    gpxBuilder.append(String.format("<instruction>%s</instruction>\n", step.getInstruction()));
                    gpxBuilder.append(String.format("<distance>%s</distance>\n", step.getDistance()));
                    gpxBuilder.append(String.format("<time>%s</time>\n", step.getTime()));
                    gpxBuilder.append("</extensions>\n");
                    gpxBuilder.append("</trkpt>\n");
                }
                gpxBuilder.append("<extensions>\n");
                gpxBuilder.append(String.format("<distance>%s</distance>\n", route.getDistance()));
                gpxBuilder.append(String.format("<time>%s</time>\n", route.getTime()));
                gpxBuilder.append("<origin>\n");
                gpxBuilder.append(formatWaypoint("wpt", navigationData.getOrigin(), "Origin"));
                gpxBuilder.append("</origin>\n");
                gpxBuilder.append("<destination>\n");
                gpxBuilder.append(formatWaypoint("wpt", navigationData.getOrigin(), "Origin"));
                gpxBuilder.append("</destination>\n");
                gpxBuilder.append("</extensions>\n");
                gpxBuilder.append("</trkseg>\n");
                gpxBuilder.append("</trk>\n");
            }

            gpxBuilder.append("</gpx>");
            return gpxBuilder.toString();
        }
    }
    public int JSONGPXData(Long id, UpdateTourForm JSON) throws ResourceException {
        try {
            NavigationData navigationData = JsonGpxConverter.parseJsonToNavigationData("path/to/sample.json");
            String gpxContent = GpxSerializer.toGpx(navigationData);
            FileUtils.writeStringToFile(gpxContent, "path/to/output.gpx");
        } catch (IOException e) {
            e.printStackTrace();
            return 1;

        }
        return 0;
    }
    private static String formatWaypoint(String tag, List<Double> coordinates, String description) {
        return String.format("<%s lat=\"%f\" lon=\"%f\">\n" +
                "<ele></ele>\n" +
                "<time></time>\n" +
                "<desc>%s</desc>\n" +
                "<type></type>\n" +
                "<extensions></extensions>\n" +
                "</%s>\n", tag, coordinates.get(1), coordinates.get(0), description, tag);
    }
}
