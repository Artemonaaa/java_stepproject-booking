package dao;

import model.Booking;
import util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingDao {
    private List<Booking> bookings = new ArrayList<>();
    private static final String FILE_PATH = "bookings.dat";

    public BookingDao() {
        List<Booking> loaded = FileUtils.loadBookings(FILE_PATH);
        if (loaded != null) bookings = loaded;
    }

    public void saveData() {
        FileUtils.saveBookings(FILE_PATH, bookings);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public boolean deleteById(String bookingId) {
        return bookings.removeIf(b -> b.getId().equals(bookingId));
    }

    public List<Booking> findByPassengerName(String name) {
        return bookings.stream()
                .filter(b -> b.getPassengerName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Booking> getAll() {
        return bookings;
    }
}
