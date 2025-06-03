package dao;

import dao.BookingDao;
import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDaoTest {

    private BookingDao dao;

    @BeforeEach
    void setup() {
        dao = new BookingDao() {
            private final List<Booking> data = new java.util.ArrayList<>();

            @Override
            public void addBooking(Booking booking) { data.add(booking); }

            @Override
            public boolean deleteById(String id) {
                return data.removeIf(b -> b.getId().equals(id));
            }

            @Override
            public List<Booking> findByPassengerName(String name) {
                return data.stream()
                        .filter(b -> b.getPassengerName().equalsIgnoreCase(name))
                        .toList();
            }

            @Override
            public List<Booking> getAll() { return data; }
        };
    }

    @Test
    void testAddAndGetAll() {
        dao.addBooking(new Booking("FL001", "Alice"));
        List<Booking> all = dao.getAll();
        assertEquals(1, all.size());
        assertEquals("Alice", all.get(0).getPassengerName());
    }

    @Test
    void testDeleteById() {
        Booking b = new Booking("FL002", "Bob");
        dao.addBooking(b);
        assertTrue(dao.deleteById(b.getId()));
        assertTrue(dao.getAll().isEmpty());
    }

    @Test
    void testFindByPassengerName() {
        dao.addBooking(new Booking("FL003", "Charlie"));
        dao.addBooking(new Booking("FL004", "Charlie"));
        List<Booking> result = dao.findByPassengerName("Charlie");
        assertEquals(2, result.size());
    }
}

