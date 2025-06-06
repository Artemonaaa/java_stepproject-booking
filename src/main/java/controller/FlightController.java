package controller;

import model.Flight;
import service.FlightService;
import exception.InvalidFlightException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    public List<Flight> getFlightsNext24Hours() {
        return flightService.getFlightsNext24Hours();
    }

    public Optional<Flight> getFlightInfo(String flightId) {
        return flightService.getFlightInfo(flightId);
    }

    public List<Flight> searchFlights(String destination, LocalDateTime date, int passengers) {
        return flightService.searchFlights(destination, date, passengers);
    }

    public boolean bookFlight(String flightId, List<String> passengerNames) {
        return flightService.bookFlight(flightId, passengerNames);
    }

    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    public Flight getFlight(String flightId) throws InvalidFlightException {
        return flightService.getFlightInfo(flightId)
                .orElseThrow(() -> new InvalidFlightException("Рейс з ID " + flightId + " не знайдено"));
    }

    public void save() {
        flightService.save();
    }
}
