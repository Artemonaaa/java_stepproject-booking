package controller;

import model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FlightService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightControllerTest {
    private FlightController flightController;
    private FlightService flightServiceMock;
    private final String testFlightId = "FL101";
    private final String testDestination = "New York";
    private final LocalDateTime testDateTime = LocalDateTime.now().plusHours(2);
    private final int testPassengers = 2;

    @BeforeEach
    void setUp() {
        flightServiceMock = mock(FlightService.class);
        flightController = new FlightController(flightServiceMock);
    }

    @Test
    void testGetFlightsNext24Hours() {
        List<Flight> expectedFlights = Arrays.asList(
                new Flight("FL101", "New York", LocalDateTime.now().plusHours(2), 150),
                new Flight("FL102", "London", LocalDateTime.now().plusHours(4), 200)
        );
        when(flightServiceMock.getFlightsNext24Hours()).thenReturn(expectedFlights);

        List<Flight> result = flightController.getFlightsNext24Hours();

        assertEquals(expectedFlights, result);
        verify(flightServiceMock).getFlightsNext24Hours();
    }

    @Test
    void testGetFlightInfo() {
        Flight expectedFlight = new Flight(testFlightId, testDestination, testDateTime, 150);
        when(flightServiceMock.getFlightInfo(testFlightId)).thenReturn(Optional.of(expectedFlight));

        Optional<Flight> result = flightController.getFlightInfo(testFlightId);

        assertTrue(result.isPresent());
        assertEquals(expectedFlight, result.get());
        verify(flightServiceMock).getFlightInfo(testFlightId);
    }

    @Test
    void testSearchFlights() {
        List<Flight> expectedFlights = Arrays.asList(
                new Flight("FL101", testDestination, testDateTime, 150),
                new Flight("FL102", testDestination, testDateTime.plusHours(2), 200)
        );
        when(flightServiceMock.searchFlights(testDestination, testDateTime, testPassengers))
                .thenReturn(expectedFlights);

        List<Flight> result = flightController.searchFlights(testDestination, testDateTime, testPassengers);

        assertEquals(expectedFlights, result);
        verify(flightServiceMock).searchFlights(testDestination, testDateTime, testPassengers);
    }

    @Test
    void testBookFlight() {
        List<String> passengerNames = Arrays.asList("John Doe", "Jane Doe");
        when(flightServiceMock.bookFlight(testFlightId, passengerNames)).thenReturn(true);

        boolean result = flightController.bookFlight(testFlightId, passengerNames);

        assertTrue(result);
        verify(flightServiceMock).bookFlight(testFlightId, passengerNames);
    }
}
