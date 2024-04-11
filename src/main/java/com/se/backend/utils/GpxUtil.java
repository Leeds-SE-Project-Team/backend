package com.se.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static com.se.backend.utils.FileUtil.saveFileToLocal;
import static com.se.backend.utils.FileUtil.stringToInputStream;

public class GpxUtil {
    static public void JSONtoGPXFile(NavigationData json, String writingPath) throws IOException {
        String gpxContent = NavigationData.toGpx(json);
        saveFileToLocal(stringToInputStream(gpxContent), writingPath);
    }

    @Getter
    public static class NavigationData {
        private String info;
        private List<Double> origin;
        private List<Double> destination;
        private int count;
        private List<Route> routes;
        private Location start;
        private Location end;
        // Getters and setters

        static String formatWaypoint(String tag, List<Double> coordinates, String description) {
            return String.format("<%s lat=\"%f\" lon=\"%f\">\n" + "<ele></ele>\n" + "<time></time>\n" + "<desc>%s</desc>\n" + "<type></type>\n" + "<extensions></extensions>\n" + "</%s>\n", tag, coordinates.get(1), coordinates.get(0), description, tag);
        }

        static String toGpx(NavigationData navigationData) {
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

        @Getter
        static public class Route {
            private int distance;
            private int time;
            private List<Step> steps;
            // Getters and setters
        }

        @Getter
        static public class Step {
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
        static public class Location {
            private List<Double> location;
            private String name;
            private String type;
            // Getters and setters
        }
    }


    class JsonGpxConverter {
        public static NavigationData parseJsonToNavigationData(String filePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(Paths.get(filePath).toFile(), NavigationData.class);
        }
    }


}
