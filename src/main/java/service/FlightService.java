package service;

import dao.FlightDAO;
import model.Flight;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlightService {
    private final FlightDAO flightDAO;

    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    public List<Flight> getFlightsNext24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);
        return flightDAO.getAllFlights().stream()
                .filter(f -> f.getDepartureTime().isAfter(now))
                .filter(f -> f.getDepartureTime().isBefore(tomorrow))
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
    }

    public Optional<Flight> getFlightInfo(String flightId) {
        return flightDAO.getFlightById(flightId);
    }

    public List<Flight> searchFlights(String destination, LocalDateTime date, int passengers) {
        return flightDAO.getAllFlights().stream()
                .filter(f -> f.getDestination().equalsIgnoreCase(destination))
                .filter(f -> f.getDepartureTime().toLocalDate().equals(date.toLocalDate()))
                .filter(f -> f.getAvailableSeats() >= passengers)
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
    }

    public boolean bookFlight(String flightId, List<String> passengerNames) {
        Optional<Flight> flightOpt = flightDAO.getFlightById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            if (flight.getAvailableSeats() >= passengerNames.size()) {
                flight.setAvailableSeats(flight.getAvailableSeats() - passengerNames.size());
                flightDAO.updateFlight(flight);
                return true;
            }
        }
        return false;
    }

    public List<Flight> getAllFlights() {
        return flightDAO.getAllFlights().stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
    }

    public void save() {
        flightDAO.save();
    }
}
