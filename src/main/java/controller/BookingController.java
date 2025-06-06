package controller;

import service.BookingService;
import service.FlightService;
import model.Flight;
import exception.BookingNotFoundException;

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
        List<Booking> bookings = bookingService.getByPassenger(passengerName);
        if (!bookings.isEmpty()) {
            System.out.println("\n=== Ваші бронювання ===");
            bookings.forEach(booking -> {
                System.out.println("-------------------");
                System.out.printf("ID бронювання: %s%n", booking.getId());
                System.out.printf("ID рейсу: %s%n", booking.getFlightId());
                System.out.printf("Пасажири: %s%n", String.join(", ", booking.getPassengerNames()));
                System.out.println("-------------------");
            });
            System.out.println("\nДля скасування бронювання використовуйте ID бронювання");
        } else {
            System.out.println("\nУ вас немає активних бронювань");
        }
        return bookings;
    }

    public boolean cancel(String bookingId) throws BookingNotFoundException {
        Booking booking = bookingService.getBookingById(bookingId);

        if (bookingService.cancelBooking(bookingId)) {
            flightService.getFlightInfo(booking.getFlightId()).ifPresent(flight -> {
                flight.setAvailableSeats(flight.getAvailableSeats() + booking.getPassengerNames().size());
                flightService.save();
            });
            return true;
        }
        return false;
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
