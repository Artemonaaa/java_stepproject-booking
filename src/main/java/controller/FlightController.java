package controller;

import model.Flight;
import service.FlightService;

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

    public boolean bookFlight(String flightId, int seats) {
        return flightService.bookSeats(flightId, seats);
    }

    public boolean cancelFlightBooking(String flightId, int seats) {
        return flightService.cancelBooking(flightId, seats);
    }
}
