package service;

import dao.BookingDao;
import model.Booking;
import exception.BookingNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingService {
    private final BookingDao bookingDao;

    public BookingService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
        migrateOldBookings();
    }

    private void migrateOldBookings() {
        List<Booking> oldBookings = bookingDao.getAllBookings();
        if (!oldBookings.isEmpty()) {
            List<Booking> newBookings = oldBookings.stream()
                    .map(booking -> new Booking(booking.getFlightId(), booking.getPassengerNames()))
                    .collect(Collectors.toList());
            bookingDao.saveAllBookings(newBookings);
            System.out.println("Старих бронювань мігровано: " + newBookings.size());
        }
    }

    public boolean bookFlight(String flightId, List<String> passengerNames) {
        List<Booking> bookings = new ArrayList<>(bookingDao.getAllBookings());
        Booking newBooking = new Booking(flightId, passengerNames);
        bookings.add(newBooking);
        bookingDao.saveAllBookings(bookings);
        return true;
    }

    public Booking getBookingById(String bookingId) throws BookingNotFoundException {
        return bookingDao.getAllBookings().stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new BookingNotFoundException("Бронювання з ID " + bookingId + " не знайдено"));
    }

    public boolean cancelBooking(String bookingId) throws BookingNotFoundException {
        List<Booking> bookings = bookingDao.getAllBookings();
        Optional<Booking> bookingOpt = bookings.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            bookings.remove(booking);
            bookingDao.saveAllBookings(bookings);
            return true;
        }
        throw new BookingNotFoundException("Бронювання з ID " + bookingId + " не знайдено");
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
