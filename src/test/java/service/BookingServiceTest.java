package service;

import dao.BookingDao;
import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        BookingDao bookingDao = new BookingDao() {
            private List<Booking> bookings = new java.util.ArrayList<>();

            @Override
            public void addBooking(Booking booking) {
                bookings.add(booking);
            }

            @Override
            public boolean deleteById(String bookingId) {
                return bookings.removeIf(b -> b.getId().equals(bookingId));
            }

            @Override
            public List<Booking> findByPassengerName(String name) {
                return bookings.stream()
                        .filter(b -> b.getPassengerName().equalsIgnoreCase(name))
                        .toList();
            }

            @Override
            public List<Booking> getAll() {
                return bookings;
            }
        };
        bookingService = new BookingService(bookingDao);
    }

    @Test
    public void testCreateBooking() {
        bookingService.createBooking("FL123", "John Doe");
        List<Booking> bookings = bookingService.findBookingsByPassenger("John Doe");
        assertEquals(1, bookings.size());
        assertEquals("FL123", bookings.get(0).getFlightId());
    }

    @Test
    public void testCancelBooking() {
        bookingService.createBooking("FL999", "Jane Doe");
        List<Booking> bookings = bookingService.findBookingsByPassenger("Jane Doe");
        assertEquals(1, bookings.size());
        String id = bookings.get(0).getId();
        boolean result = bookingService.cancelBooking(id);
        assertTrue(result);
        assertTrue(bookingService.findBookingsByPassenger("Jane Doe").isEmpty());
    }

    @Test
    public void testFindBookingsByPassenger() {
        bookingService.createBooking("FL001", "Alice");
        bookingService.createBooking("FL002", "Alice");
        List<Booking> bookings = bookingService.findBookingsByPassenger("Alice");
        assertEquals(2, bookings.size());
    }

    @Test
    public void testCancelNonexistentBooking() {
        boolean result = bookingService.cancelBooking("nonexistent-id");
        assertFalse(result);
    }
}
