package controller;

import model.Booking;
import service.BookingService;

import java.util.List;

public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void book(String flightId, String passengerName) {
        bookingService.createBooking(flightId, passengerName);
    }

    public boolean cancel(String bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    public List<Booking> getByPassenger(String name) {
        return bookingService.findBookingsByPassenger(name);
    }

    public void save() {
        bookingService.save();
    }
}

