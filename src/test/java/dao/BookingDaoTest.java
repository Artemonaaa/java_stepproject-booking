package dao;

import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BookingDaoTest {

    private BookingDao bookingDao;

    @BeforeEach
    void setup() {
        bookingDao = new BookingDao();
    }

    @Test
    void testGetAllBookings() {
        List<Booking> mockBookings = new ArrayList<>();
        mockBookings.add(new Booking("FL001", List.of("Alice")));

        try (MockedStatic<util.FileUtils> mockedFileUtils = Mockito.mockStatic(util.FileUtils.class)) {
            mockedFileUtils.when(() -> util.FileUtils.loadBookings("bookings.dat"))
                    .thenReturn(mockBookings);

            List<Booking> bookings = bookingDao.getAllBookings();
            assertEquals(1, bookings.size());
            assertTrue(bookings.get(0).getPassengerNames().contains("Alice"));
        }
    }

    @Test
    void testSaveAllBookings() {
        List<Booking> bookingsToSave = new ArrayList<>();
        bookingsToSave.add(new Booking("FL002", List.of("Alice")));

        try (MockedStatic<util.FileUtils> mockedFileUtils = Mockito.mockStatic(util.FileUtils.class)) {
            mockedFileUtils.when(() -> util.FileUtils.saveBookings("bookings.dat", bookingsToSave))
                    .thenAnswer(invocation -> null);

            bookingDao.saveAllBookings(bookingsToSave);

            mockedFileUtils.verify(() -> util.FileUtils.saveBookings("bookings.dat", bookingsToSave), times(1));
        }
    }
}
