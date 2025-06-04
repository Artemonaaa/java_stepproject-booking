package dao;

import model.Booking;

import java.util.List;

public class BookingDao {
    private static final String BOOKINGS_FILE = "bookings.dat";

    public List<Booking> getAllBookings() {
        return util.FileUtils.loadBookings(BOOKINGS_FILE);
    }

    public void saveAllBookings(List<Booking> bookings) {
        util.FileUtils.saveBookings(BOOKINGS_FILE, bookings);
    }
}
