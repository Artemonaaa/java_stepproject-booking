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
        return flightDAO.findFlights(
                destination == null || destination.isEmpty() ? null : destination,
                date,
                passengers
        );
    }

    public boolean bookSeats(String flightId, int seats) {
        Optional<Flight> flightOpt = flightDAO.getFlightById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            if (flight.getAvailableSeats() >= seats) {
                flight.setAvailableSeats(flight.getAvailableSeats() - seats);
                return flightDAO.updateFlight(flight);
            }
        }
        return false;
    }

    public boolean cancelBooking(String flightId, int seats) {
        Optional<Flight> flightOpt = flightDAO.getFlightById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            flight.setAvailableSeats(flight.getAvailableSeats() + seats);
            return flightDAO.updateFlight(flight);
        }
        return false;
    }
}
