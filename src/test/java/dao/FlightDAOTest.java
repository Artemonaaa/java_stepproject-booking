package dao;

import model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlightDAOTest {
    private FlightDAO flightDAO;
    private final String testFlightId = "FL101";
    private final LocalDateTime testTime = LocalDateTime.now().plusHours(2);

    @BeforeEach
    void setUp() {
        new File(FlightDAO.FLIGHTS_FILE).delete();
        flightDAO = new FlightDAO();
    }

    @Test
    void testGetAllFlights() {
        List<Flight> flights = flightDAO.getAllFlights();
        assertFalse(flights.isEmpty());
    }

    @Test
    void testGetFlightById_Exists() {
        Optional<Flight> flight = flightDAO.getFlightById(testFlightId);
        assertTrue(flight.isPresent());
        assertEquals(testFlightId, flight.get().getId());
    }

    @Test
    void testGetFlightById_NotExists() {
        Optional<Flight> flight = flightDAO.getFlightById("NON_EXISTENT");
        assertFalse(flight.isPresent());
    }

    @Test
    void testFindFlights_ByDestination() {
        List<Flight> flights = flightDAO.findFlights("London", null, 1);
        assertFalse(flights.isEmpty());
        flights.forEach(f -> assertEquals("London", f.getDestination()));
    }

    @Test
    void testFindFlights_ByDate() {
        LocalDateTime date = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0);
        List<Flight> flights = flightDAO.findFlights(null, date, 1);
        assertFalse(flights.isEmpty());
        flights.forEach(f -> assertEquals(date.toLocalDate(), f.getDepartureTime().toLocalDate()));
    }

    @Test
    void testFindFlights_BySeats() {
        List<Flight> flights = flightDAO.findFlights(null, null, 200);
        assertFalse(flights.isEmpty());
        flights.forEach(f -> assertTrue(f.getAvailableSeats() >= 200));
    }

    @Test
    void testUpdateFlight() {
        Flight flight = flightDAO.getFlightById(testFlightId).get();
        int originalSeats = flight.getAvailableSeats();
        flight.setAvailableSeats(originalSeats - 1);

        assertTrue(flightDAO.updateFlight(flight));

        Flight updatedFlight = flightDAO.getFlightById(testFlightId).get();
        assertEquals(originalSeats - 1, updatedFlight.getAvailableSeats());
    }

    @Test
    void testUpdateFlight_NotExists() {
        Flight flight = new Flight("NON_EXISTENT", "Test", testTime, 100);
        assertFalse(flightDAO.updateFlight(flight));
    }

    @Test
    void testPersistence() {
        // Отримуємо початковий стан рейсу
        Flight flight = flightDAO.getFlightById(testFlightId).get();
        int originalSeats = flight.getAvailableSeats();

        // Змінюємо кількість місць
        int newSeats = originalSeats - 1;
        flight.setAvailableSeats(newSeats);
        flightDAO.updateFlight(flight);

        // Створюємо новий DAO, який має завантажити дані з файлу
        FlightDAO newDao = new FlightDAO();
        Flight reloadedFlight = newDao.getFlightById(testFlightId).get();

        assertEquals(newSeats, reloadedFlight.getAvailableSeats());
    }
}
