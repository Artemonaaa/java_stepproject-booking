package service;

import dao.BookingDao;
import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    private BookingService bookingService;
    private BookingDao bookingDaoMock;

    @BeforeEach
    public void setUp() {
        bookingDaoMock = mock(BookingDao.class);
        bookingService = new BookingService(bookingDaoMock);
    }

    @Test
    public void testBookFlight() {
        List<Booking> bookings = new ArrayList<>();
        when(bookingDaoMock.getAllBookings()).thenReturn(bookings);

        boolean result = bookingService.bookFlight("FL123", List.of("John Doe"));
        assertTrue(result);

        verify(bookingDaoMock).saveAllBookings(anyList());
    }

    @Test
    public void testCancelBooking() {
        Booking booking = new Booking("FL123", List.of("John Doe"));
        String bookingId = booking.getId();
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        when(bookingDaoMock.getAllBookings()).thenReturn(bookings);

        boolean result = bookingService.cancelBooking(bookingId);
        assertTrue(result);

        verify(bookingDaoMock).saveAllBookings(anyList());
    }

    @Test
    public void testCancelNonexistentBooking() {
        List<Booking> bookings = new ArrayList<>();
        when(bookingDaoMock.getAllBookings()).thenReturn(bookings);

        boolean result = bookingService.cancelBooking("nonexistent-id");
        assertFalse(result);

        verify(bookingDaoMock, never()).saveAllBookings(anyList());
    }

    @Test
    public void testGetByPassenger() {
        Booking booking1 = new Booking("FL001", List.of("Alice"));
        Booking booking2 = new Booking("FL002", List.of("Alice"));
        List<Booking> bookings = List.of(booking1, booking2);
        when(bookingDaoMock.getAllBookings()).thenReturn(bookings);

        List<Booking> result = bookingService.getByPassenger("Alice");
        assertEquals(2, result.size());
    }
}
