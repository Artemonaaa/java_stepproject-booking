import console.ConsoleUI;
import controller.BookingController;
import controller.FlightController;
import dao.BookingDao;
import dao.FlightDAO;
import service.BookingService;
import service.FlightService;

public class Main {
    public static void main(String[] args) {
        FlightDAO flightDAO = new FlightDAO();
        BookingDao bookingDao = new BookingDao();

        FlightService flightService = new FlightService(flightDAO);
        BookingService bookingService = new BookingService(bookingDao);

        FlightController flightController = new FlightController(flightService);
        BookingController bookingController = new BookingController(bookingService, flightService);

        ConsoleUI ui = new ConsoleUI(bookingController, flightController);
        ui.start();
    }
}
