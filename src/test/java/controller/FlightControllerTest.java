package controller;

import model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FlightService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightControllerTest {
    private FlightController flightController;
    private FlightService flightServiceMock;
    private final String testFlightId = "FL101";
    private final LocalDateTime testTime = LocalDateTime.now().plusHours(2);

    @BeforeEach
    void setUp() {
        flightServiceMock = mock(FlightService.class);
        flightController = new FlightController(flightServiceMock);
    }

    @Test
    void testGetFlightsNext24Hours() {
        List<Flight> mockFlights = List.of(
                new Flight("FL101", "London", testTime, 200)
        );

        when(flightServiceMock.getFlightsNext24Hours()).thenReturn(mockFlights);

        List<Flight> result = flightController.getFlightsNext24Hours();
        assertEquals(1, result.size());
        assertEquals("FL101", result.get(0).getId());
    }

    @Test
    void testGetFlightInfo() {
        Flight mockFlight = new Flight(testFlightId, "London", testTime, 200);
        when(flightServiceMock.getFlightInfo(testFlightId)).thenReturn(Optional.of(mockFlight));

        Optional<Flight> result = flightController.getFlightInfo(testFlightId);
        assertTrue(result.isPresent());
        assertEquals(testFlightId, result.get().getId());
    }

    @Test
    void testSearchFlights() {
        List<Flight> expectedFlights = List.of(
                new Flight("FL101", "London", testTime, 200)
        );

        when(flightServiceMock.searchFlights(
                eq("London"),
                any(LocalDateTime.class),
                eq(2))
        ).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightController.searchFlights(
                "London",
                LocalDateTime.now(),
                2);

        assertEquals(1, actualFlights.size());
        assertEquals("London", actualFlights.get(0).getDestination());
    }

    @Test
    void testBookFlight() {
        when(flightServiceMock.bookSeats(testFlightId, 2)).thenReturn(true);

        boolean result = flightController.bookFlight(testFlightId, 2);
        assertTrue(result);
        verify(flightServiceMock).bookSeats(testFlightId, 2);
    }

    @Test
    void testCancelFlightBooking() {
        when(flightServiceMock.cancelBooking(testFlightId, 2)).thenReturn(true);

        boolean result = flightController.cancelFlightBooking(testFlightId, 2);
        assertTrue(result);
        verify(flightServiceMock).cancelBooking(testFlightId, 2);
    }
}
