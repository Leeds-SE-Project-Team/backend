package com.se.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.models.PON;
import com.se.backend.services.TourService;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.se.backend.utils.FileUtil.saveFileToLocal;
import static com.se.backend.utils.FileUtil.stringToInputStream;

public class GpxUtil {
    static public void JSONtoGPXFile(TourService.CreateTourForm form, String writingPath) throws IOException {
        String gpxContent = NavigationData.toGpx(form);
        saveFileToLocal(stringToInputStream(gpxContent), writingPath);
    }

    @Setter
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

        static String toGpx(TourService.CreateTourForm form) {

            NavigationData navigationData = form.getResult();
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
                gpxBuilder.append(String.format("<type>%s</type>\n", navigationData.getCount()));
                gpxBuilder.append("<trkseg>\n");
                for (Step step : route.getSteps()) {
                    gpxBuilder.append("<trkpt>\n");
                    for (List<Double> point : step.getPath()) {
                        gpxBuilder.append(formatWaypoint("wpt", point, ""));
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
                gpxBuilder.append(formatWaypoint("wpt", navigationData.getDestination(), "Destination"));
                gpxBuilder.append("</destination>\n");
                gpxBuilder.append("<pon>\n");

                for (PON pon : form.getPons()) {
                    List<Double> locationList = pon.getLocationAsList(); // This will parse the string into a List<Double>
                    gpxBuilder.append(formatWaypoint("wpt", locationList, "PON"));
                }
                gpxBuilder.append("</pon>\n");
                gpxBuilder.append("</extensions>\n");
                gpxBuilder.append("</trkseg>\n");
                gpxBuilder.append("</trk>\n");
            }

            gpxBuilder.append("</gpx>");
            return gpxBuilder.toString();
        }

        @Setter
        @Getter
        static public class Route {
            private int distance;
            private int time;
            private List<Step> steps;
            // Getters and setters
        }
        @Setter
        @Getter
        static public class WayPoint {
            boolean isWaypoint;
            private List<Double> location;
            private String name;
            private String type;
            private int sequence;
            // Getters and setters
        }

        @Getter
        @Setter
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

    public static class GpxToNavigationDataConverter {
        public static TourService.CreateTourForm parseGpxToNavigationData(String filePath) throws Exception {
            TourService.CreateTourForm form = new TourService.CreateTourForm();
            Path filePath1 = Paths.get(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(Files.newInputStream(filePath1));
            doc.getDocumentElement().normalize();

            // Assume a structure similar to the one used to create GPX in your toGpx method
            NavigationData navigationData = new NavigationData();
            // Extracting information from the GPX file
            NodeList trkList = doc.getElementsByTagName("trk");
            List<NavigationData.Route> routes = new ArrayList<>();
            List<PON> attachedPONs = new ArrayList<>();
//            List<NavigationData.WayPoint> wayPoints = new ArrayList<>();
            for (int i = 0; i < trkList.getLength(); i++) {
                Element trkElement = (Element) trkList.item(i);
                NavigationData.Route route = new NavigationData.Route();
                List<NavigationData.Step> steps = new ArrayList<>();
                NodeList trksegList = trkElement.getElementsByTagName("trkseg");
                for (int j = 0; j < trksegList.getLength(); j++) {
                    Element trksegElement = (Element) trksegList.item(j);
                    NodeList trkptList = trksegElement.getElementsByTagName("trkpt");
                    for (int k = 0; k < trkptList.getLength(); k++) {
                        NavigationData.Step step = new NavigationData.Step();
                        Element trkptElement = (Element) trkptList.item(k);
                        NodeList wptList = trkptElement.getElementsByTagName("wpt");
                        List<List<Double>> path = new ArrayList<>();
                        for (int l = 0; l < wptList.getLength(); l++) {
                            if (l == 0) {
                                step.setStart_location(List.of(Double.parseDouble(trkptElement.getAttribute("lat")), Double.parseDouble(trkptElement.getAttribute("lon"))));
                            }
                            if (l == wptList.getLength()) {
                                step.setEnd_location(List.of(Double.parseDouble(trkptElement.getAttribute("lat")), Double.parseDouble(trkptElement.getAttribute("lon"))));
                            }
                            Element wptElement = (Element) wptList.item(l); // 从 NodeList 中获取每个 Element
                            Double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                            Double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                            List<Double> point = Arrays.asList(lat, lon); // 创建一个包含经度和纬度的列表
                            path.add(point); // 将点添加到路径列表中
                        }
                        step.setPath(path); // 将路径设置到步骤中
                        // Assuming each trkpt can have extensions such as instruction and distance
                        Element extensionstrkptElement = (Element) trkptElement.getElementsByTagName("extensions").item(0);
                        if (extensionstrkptElement != null) {
                            String instruction = extensionstrkptElement.getElementsByTagName("instruction").item(0).getTextContent();
                            step.setInstruction(instruction);
                            step.setDistance(Integer.parseInt(extensionstrkptElement.getElementsByTagName("distance").item(0).getTextContent()));
                            step.setTime(Integer.parseInt(extensionstrkptElement.getElementsByTagName("time").item(0).getTextContent()));
                            // Parsing the road from the instruction
                        }
                        steps.add(step);
                    }

                    Element extensionstrksegElement = (Element) trksegElement.getElementsByTagName("extensions").item(0);
                    if (extensionstrksegElement != null) {
                        route.setDistance(Integer.parseInt(extensionstrksegElement.getElementsByTagName("distance").item(0).getTextContent()));
                        route.setTime(Integer.parseInt(extensionstrksegElement.getElementsByTagName("time").item(0).getTextContent()));
                        //originElement
                        Element originElement = (Element) extensionstrksegElement.getElementsByTagName("origin").item(0);
                        NodeList originwptList = originElement.getElementsByTagName("wpt");
                        for (int l = 0; l < originwptList.getLength(); l++) {
                            Element wptElement = (Element) originwptList.item(l); // 从 NodeList 中获取每个 Element
                            Double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                            Double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                            navigationData.setOrigin(List.of(lat, lon));
                        }
                        Element ponElement = (Element) extensionstrksegElement.getElementsByTagName("pon").item(0);
                        NodeList ponwptList = ponElement.getElementsByTagName("wpt");
//                        NavigationData.WayPoint wayPoint = new NavigationData.WayPoint();
                        for (int l = 0; l < ponwptList.getLength(); l++) {
                            PON pon = new PON();
                            Element wptElement = (Element) ponwptList.item(l); // 从 NodeList 中获取每个 Element
                            Double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                            Double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                            pon.setLocation(List.of(lat, lon).toString());
                            pon.setName("PON");
                            pon.setSequence(l+1);
                            attachedPONs.add(pon);
                        }
                        //destinationElement
                        Element destinationElement = (Element) extensionstrksegElement.getElementsByTagName("destination").item(0);
                        NodeList destinationwptList = destinationElement.getElementsByTagName("wpt");
                        for (int l = 0; l < destinationwptList.getLength(); l++) {
                            Element wptElement = (Element) destinationwptList.item(l); // 从 NodeList 中获取每个 Element
                            Double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                            Double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                            navigationData.setOrigin(List.of(lat, lon));
                        }
                    }

                }
                route.setSteps(steps);
                routes.add(route);
            }
            navigationData.setRoutes(routes);
            form.setPons(attachedPONs);
            form.setResult(navigationData);
            form.setStartLocation(navigationData.getOrigin().toString());
            form.setEndLocation(navigationData.getDestination().toString());

            return form;
        }
    }

    public class JsonGpxConverter {
        public static NavigationData parseJsonToNavigationData(String filePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(Paths.get(filePath).toFile(), NavigationData.class);
        }

    }


}
