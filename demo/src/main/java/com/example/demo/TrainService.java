package com.example.demo;

import com.google.transit.realtime.GtfsRealtime.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class TrainService {

    private final Map<String, String> stopNames = new HashMap<>();
    private final Map<String, double[]> stopCoords = new HashMap<>();
    private final Map<String, String> shapeToRoute = new HashMap<>();
    private final Map<String, String> tripToRoute = new HashMap<>();

    public TrainService() throws Exception {
        var stream = getClass().getResourceAsStream("/stops.txt");
        var reader = new BufferedReader(new InputStreamReader(stream));
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.replace("\"", "").split(",");
            if (parts.length >= 5) {
                String id = parts[0].trim();
                String name = parts[2].trim();
                double lat = Double.parseDouble(parts[3].trim());
                double lon = Double.parseDouble(parts[4].trim());
                stopNames.put(id, name);
                stopCoords.put(id, new double[]{lat, lon});
            }
        }

        var tripsStream = getClass().getResourceAsStream("/trips.txt");
        var tripsReader = new BufferedReader(new InputStreamReader(tripsStream));
        tripsReader.readLine();
        String tripsLine;
        while ((tripsLine = tripsReader.readLine()) != null) {
            String[] parts = tripsLine.replace("\"", "").split(",");
            if (parts.length >= 7) {
                String routeId = parts[0].trim();
                String tripId = parts[2].trim();
                String shapeId = parts[6].trim();
                shapeToRoute.put(shapeId, routeId);
                tripToRoute.put(tripId, routeId);
            }
        }
    }

    public List<TrainUpdate> getLiveTrainData() throws Exception {
        URL url = new URL("https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/lirr%2Fgtfs-lirr");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        FeedMessage feed = FeedMessage.parseFrom(connection.getInputStream());
        List<TrainUpdate> trains = new ArrayList<>();

        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasTripUpdate()) {
                TripUpdate trip = entity.getTripUpdate();
                String tripId = trip.getTrip().getTripId();
                String routeId = trip.getTrip().getRouteId();
                if (routeId == null || routeId.isEmpty()) {
                    routeId = tripToRoute.getOrDefault(tripId, "");
                }

                for (TripUpdate.StopTimeUpdate stop : trip.getStopTimeUpdateList()) {
                    String stopId = stop.getStopId();
                    String stopName = stopNames.getOrDefault(stopId, "Stop " + stopId);
                    long delay = stop.hasArrival() ? stop.getArrival().getDelay() : 0;
                    double[] coords = stopCoords.getOrDefault(stopId, new double[]{0, 0});
                    trains.add(new TrainUpdate(tripId, stopName, delay, coords[0], coords[1], routeId));
                }
            }
        }

        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasVehicle()) {
                VehiclePosition vehicle = entity.getVehicle();
                String tripId = vehicle.getTrip().getTripId();
                String routeId = vehicle.getTrip().getRouteId();
                if (routeId == null || routeId.isEmpty()) {
                    routeId = tripToRoute.getOrDefault(tripId, "");
                }
                double lat = vehicle.getPosition().getLatitude();
                double lon = vehicle.getPosition().getLongitude();
                String stopId = vehicle.getStopId();
                String stopName = stopNames.getOrDefault(stopId, "Unknown");
                trains.add(new TrainUpdate(tripId, stopName, 0, lat, lon, routeId));
            }
        }

        return trains;
    }

    public Map<String, List<double[]>> getShapes() throws Exception {
        var stream = getClass().getResourceAsStream("/shapes.txt");
        var reader = new BufferedReader(new InputStreamReader(stream));
        reader.readLine();
        Map<String, List<double[]>> shapes = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.replace("\"", "").split(",");
            if (parts.length >= 4) {
                String shapeId = parts[0].trim();
                double lat = Double.parseDouble(parts[1].trim());
                double lon = Double.parseDouble(parts[2].trim());
                shapes.computeIfAbsent(shapeId, k -> new ArrayList<>()).add(new double[]{lat, lon});
            }
        }
        return shapes;
    }

    public Map<String, Object> getShapesWithRoutes() throws Exception {
        Map<String, List<double[]>> shapes = getShapes();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<double[]>> entry : shapes.entrySet()) {
            String routeId = shapeToRoute.getOrDefault(entry.getKey(), "12");
            Map<String, Object> shapeData = new HashMap<>();
            shapeData.put("points", entry.getValue());
            shapeData.put("route", routeId);
            result.put(entry.getKey(), shapeData);
        }
        return result;
    }
}

class TrainUpdate {
    public String tripId;
    public String stop;
    public long delay;
    public double lat;
    public double lon;
    public String route;

    public TrainUpdate(String tripId, String stop, long delay, double lat, double lon, String route) {
        this.tripId = tripId;
        this.stop = stop;
        this.delay = delay;
        this.lat = lat;
        this.lon = lon;
        this.route = route;
    }
}