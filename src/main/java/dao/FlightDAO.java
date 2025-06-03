package dao;

import model.Flight;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlightDAO {
    public static final String FLIGHTS_FILE = "flights.txt";
    private static final String DELIMITER = "|";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Map<String, Flight> flights = new HashMap<>();

    public FlightDAO() {
        loadFlights();
        if (flights.isEmpty()) {
            generateSampleFlights();
        }
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }

    public Optional<Flight> getFlightById(String id) {
        return Optional.ofNullable(flights.get(id));
    }

    public List<Flight> findFlights(String destination, LocalDateTime date, int passengers) {
        return flights.values().stream()
                .filter(f -> destination == null || f.getDestination().equalsIgnoreCase(destination))
                .filter(f -> date == null || f.getDepartureTime().toLocalDate().equals(date.toLocalDate()))
                .filter(f -> f.getAvailableSeats() >= passengers)
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
    }

    public boolean updateFlight(Flight flight) {
        if (flights.containsKey(flight.getId())) {
            flights.put(flight.getId(), flight);
            saveFlights();
            return true;
        }
        return false;
    }

    private void generateSampleFlights() {
        LocalDateTime now = LocalDateTime.now();
        String[] destinations = {"New York", "London", "Paris", "Tokyo", "Berlin", "Rome", "Madrid", "Sydney"};

        for (int i = 0; i < 20; i++) {
            String id = "FL" + (100 + i);
            String dest = destinations[i % destinations.length];
            LocalDateTime time = now.plusHours(i * 2).withMinute(0).withSecond(0);
            int seats = 150 + (i * 10) % 100;

            flights.put(id, new Flight(id, dest, time, seats));
        }
        saveFlights();
    }

    private void saveFlights() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FLIGHTS_FILE))) {
            for (Flight flight : flights.values()) {
                String line = String.join(DELIMITER,
                        flight.getId(),
                        flight.getDestination(),
                        flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                        String.valueOf(flight.getTotalSeats()),
                        String.valueOf(flight.getAvailableSeats())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving flights: " + e.getMessage());
        }
    }

    private void loadFlights() {
        if (!Files.exists(Paths.get(FLIGHTS_FILE))) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length == 5) {
                    String id = parts[0];
                    String destination = parts[1];
                    LocalDateTime departureTime = LocalDateTime.parse(parts[2], DATE_TIME_FORMATTER);
                    int totalSeats = Integer.parseInt(parts[3]);
                    int availableSeats = Integer.parseInt(parts[4]);

                    Flight flight = new Flight(id, destination, departureTime, totalSeats);
                    flight.setAvailableSeats(availableSeats);
                    flights.put(id, flight);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading flights: " + e.getMessage());
        }
    }
}
