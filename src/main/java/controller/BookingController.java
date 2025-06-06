package controller;

import service.BookingService;
import service.FlightService;
import model.Flight;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Booking;

public class BookingController {
    private final BookingService bookingService;
    private final FlightService flightService;

    public BookingController(BookingService bookingService, FlightService flightService) {
        this.bookingService = bookingService;
        this.flightService = flightService;
    }

    public boolean book(String flightId, List<String> passengerNames) {
        if (flightService.bookFlight(flightId, passengerNames)) {
            bookingService.bookFlight(flightId, passengerNames);
            return true;
        }
        return false;
    }

    public List<Booking> getByPassenger(String passengerName) {
        return bookingService.getByPassenger(passengerName);
    }

    public boolean cancel(String bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    public void save() {
        bookingService.save();
    }

    public List<Flight> getMyFlights(String passengerName) {
        return getByPassenger(passengerName).stream()
                .map(booking -> flightService.getFlightInfo(booking.getFlightId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
