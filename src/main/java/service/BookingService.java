package service;

import dao.BookingDao;
import exception.BookingNotFoundException;
import model.Booking;

import java.util.List;

public class BookingService {
    private final BookingDao bookingDao;

    public BookingService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    public void createBooking(String flightId, String passengerName) {
        bookingDao.addBooking(new Booking(flightId, passengerName));
    }

    public boolean cancelBooking(String bookingId) {
        boolean removed = bookingDao.deleteById(bookingId);
        if (!removed) {
            throw new BookingNotFoundException("Booking with ID " + bookingId + " not found.");
        }
        return true;
    }

    public List<Booking> findBookingsByPassenger(String name) {
        return bookingDao.findByPassengerName(name);
    }

    public void save() {
        bookingDao.saveData();
    }
}