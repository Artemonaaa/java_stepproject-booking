package controller;

import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookingService;
import service.FlightService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingControllerTest {

    private BookingService mockBookingService;
    private FlightService mockFlightService;
    private BookingController controller;

    @BeforeEach
    void setup() {
        mockBookingService = mock(BookingService.class);
        mockFlightService = mock(FlightService.class);
        controller = new BookingController(mockBookingService, mockFlightService);
    }

    @Test
    void testBookDelegatesToServices() {
        List<String> passengers = List.of("Test User");
        when(mockFlightService.bookFlight("FL123", passengers)).thenReturn(true);

        assertTrue(controller.book("FL123", passengers));
        verify(mockFlightService, times(1)).bookFlight("FL123", passengers);
        verify(mockBookingService, times(1)).bookFlight("FL123", passengers);
    }

    @Test
    void testCancelDelegatesToService() {
        when(mockBookingService.cancelBooking("abc")).thenReturn(true);

        assertTrue(controller.cancel("abc"));
        verify(mockBookingService).cancelBooking("abc");
    }

    @Test
    void testGetByPassengerDelegatesToService() {
        List<Booking> dummyList = new ArrayList<>();
        dummyList.add(new Booking("FL200", List.of("Jane Doe")));
        when(mockBookingService.getByPassenger("Jane Doe")).thenReturn(dummyList);

        List<Booking> result = controller.getByPassenger("Jane Doe");
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPassengerNames().contains("Jane Doe"));
    }
}
