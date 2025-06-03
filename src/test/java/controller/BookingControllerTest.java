package controller;

import controller.BookingController;
import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookingService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingControllerTest {

    private BookingService mockService;
    private BookingController controller;

    @BeforeEach
    void setup() {
        mockService = mock(BookingService.class);
        controller = new BookingController(mockService);
    }

    @Test
    void testBookDelegatesToService() {
        controller.book("FL123", "Test User");
        verify(mockService, times(1)).createBooking("FL123", "Test User");
    }

    @Test
    void testCancelDelegatesToService() {
        when(mockService.cancelBooking("abc")).thenReturn(true);
        assertTrue(controller.cancel("abc"));
        verify(mockService).cancelBooking("abc");
    }

    @Test
    void testGetByPassengerDelegatesToService() {
        List<Booking> dummyList = new ArrayList<>();
        dummyList.add(new Booking("FL200", "Jane Doe"));
        when(mockService.findBookingsByPassenger("Jane Doe")).thenReturn(dummyList);

        List<Booking> result = controller.getByPassenger("Jane Doe");
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getPassengerName());
    }
}
