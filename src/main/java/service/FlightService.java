package service;

import dao.FlightDAO;
import model.Booking;
import model.Flight;
import util.FileUtils;

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

    public boolean cancelBooking(String bookingId) {
        List<Booking> bookings = FileUtils.loadBookings("bookings.dat");
        if (bookings != null) {
            Optional<Booking> bookingOpt = bookings.stream()
                    .filter(b -> b.getId().equals(bookingId))
                    .findFirst();
            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                bookings.remove(booking);
                FileUtils.saveBookings("bookings.dat", bookings);
                // Update flight seats
                Optional<Flight> flightOpt = flightDAO.getFlightById(booking.getFlightId());
                flightOpt.ifPresent(flight -> {
                    flight.setAvailableSeats(flight.getAvailableSeats() + booking.getPassengerNames().size());
                    flightDAO.updateFlight(flight);
                });
                return true;
            }
        }
        return false;
    }

    public List<Booking> getBookingsByPassenger(String name) {
        List<Booking> bookings = FileUtils.loadBookings("bookings.dat");
        if (bookings != null) {
            return bookings.stream()
                    .filter(b -> b.getPassengerNames().contains(name))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
