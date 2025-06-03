package service;

import dao.FlightDAO;
import model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {
    private FlightService flightService;
    private FlightDAO flightDAOMock;
    private final String testFlightId = "FL101";
    private final LocalDateTime testTime = LocalDateTime.now().plusHours(2);

    @BeforeEach
    void setUp() {
        flightDAOMock = mock(FlightDAO.class);
        flightService = new FlightService(flightDAOMock);

        Flight testFlight = new Flight(testFlightId, "London", testTime, 200);
        when(flightDAOMock.getFlightById(testFlightId)).thenReturn(Optional.of(testFlight));
        when(flightDAOMock.updateFlight(testFlight)).thenReturn(true);
    }

    @Test
    void testGetFlightsNext24Hours() {
        List<Flight> mockFlights = List.of(
                new Flight("FL101", "London", LocalDateTime.now().plusHours(1), 200),
                new Flight("FL102", "Paris", LocalDateTime.now().plusHours(25), 200)
        );

        when(flightDAOMock.getAllFlights()).thenReturn(mockFlights);

        List<Flight> result = flightService.getFlightsNext24Hours();
        assertEquals(1, result.size());
        assertEquals("FL101", result.get(0).getId());
    }

    @Test
    void testGetFlightInfo_Exists() {
        Optional<Flight> result = flightService.getFlightInfo(testFlightId);
        assertTrue(result.isPresent());
        assertEquals(testFlightId, result.get().getId());
    }

    @Test
    void testGetFlightInfo_NotExists() {
        when(flightDAOMock.getFlightById("NON_EXISTENT")).thenReturn(Optional.empty());
        Optional<Flight> result = flightService.getFlightInfo("NON_EXISTENT");
        assertFalse(result.isPresent());
    }

    @Test
    void testSearchFlights() {
        List<Flight> expectedFlights = List.of(
                new Flight("FL101", "London", LocalDateTime.now().plusDays(1), 200)
        );

        when(flightDAOMock.findFlights(
                eq("London"),
                any(LocalDateTime.class),
                eq(2))
        ).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightService.searchFlights(
                "London",
                LocalDateTime.now().plusDays(1),
                2);

        assertEquals(expectedFlights.size(), actualFlights.size());
        assertEquals("London", actualFlights.get(0).getDestination());
    }

    @Test
    void testBookSeats_Success() {
        Flight testFlight = new Flight(testFlightId, "London", testTime, 200);
        when(flightDAOMock.getFlightById(testFlightId)).thenReturn(Optional.of(testFlight));

        boolean result = flightService.bookSeats(testFlightId, 2);
        assertTrue(result);
        assertEquals(198, testFlight.getAvailableSeats());
        verify(flightDAOMock).updateFlight(testFlight);
    }

    @Test
    void testBookSeats_NotEnoughSeats() {
        Flight testFlight = new Flight(testFlightId, "London", testTime, 1);
        when(flightDAOMock.getFlightById(testFlightId)).thenReturn(Optional.of(testFlight));

        boolean result = flightService.bookSeats(testFlightId, 2);
        assertFalse(result);
        assertEquals(1, testFlight.getAvailableSeats());
        verify(flightDAOMock, never()).updateFlight(testFlight);
    }

    @Test
    void testCancelBooking() {
        Flight testFlight = new Flight(testFlightId, "London", testTime, 198);
        when(flightDAOMock.getFlightById(testFlightId)).thenReturn(Optional.of(testFlight));

        boolean result = flightService.cancelBooking(testFlightId, 2);
        assertTrue(result);
        assertEquals(200, testFlight.getAvailableSeats());
        verify(flightDAOMock).updateFlight(testFlight);
    }
}
