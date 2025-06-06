package service;

import dao.BookingDao;
import model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingService {
    private final BookingDao bookingDao;
    private final List<String> bookings = new ArrayList<>();

    public BookingService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    public boolean bookFlight(String flightId, List<String> passengerNames) {
        List<Booking> bookings = new ArrayList<>(bookingDao.getAllBookings());
        Booking newBooking = new Booking(flightId, passengerNames);
        bookings.add(newBooking);
        bookingDao.saveAllBookings(bookings);
        return true;
    }

    public boolean cancelBooking(String bookingId) {
        List<Booking> bookings = bookingDao.getAllBookings();
        Optional<Booking> bookingOpt = bookings.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();
        if (bookingOpt.isPresent()) {
            bookings.remove(bookingOpt.get());
            bookingDao.saveAllBookings(bookings);
            return true;
        }
        return false;
    }

    public List<Booking> getByPassenger(String name) {
        return bookingDao.getAllBookings().stream()
                .filter(b -> b.getPassengerNames().contains(name))
                .collect(Collectors.toList());
    }

    public void save() {
        bookingDao.saveAllBookings(bookingDao.getAllBookings());
    }
}
