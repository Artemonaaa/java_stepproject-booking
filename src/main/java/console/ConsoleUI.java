package console;

import controller.BookingController;
import controller.FlightController;
import model.Flight;
import model.Booking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final FlightController flightController;
    private final BookingController bookingController;
    private final Scanner scanner;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI(FlightController flightController, BookingController bookingController) {
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== ГОЛОВНЕ МЕНЮ ===");
            System.out.println("1. Онлайн табло (рейси в найближчі 24 години)");
            System.out.println("2. Переглянути інформацію про рейс");
            System.out.println("3. Пошук та бронювання рейсу");
            System.out.println("4. Скасувати бронювання");
            System.out.println("5. Мої рейси");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> showOnlineBoard();
                case "2" -> showFlightInfo();
                case "3" -> searchAndBookFlight();
                case "4" -> cancelBooking();
                case "5" -> myBookings();
                case "0" -> {
                    System.out.println("До побачення!");
                    return;
                }
                default -> System.out.println("Невідома команда. Спробуйте ще раз.");
            }
        }
    }

    private void showOnlineBoard() {
        List<Flight> flights = flightController.getFlightsNext24Hours();
        if (flights.isEmpty()) {
            System.out.println("Немає доступних рейсів на найближчі 24 години.");
        } else {
            flights.forEach(flight -> System.out.printf("%s - %s до %s (%d/%d місць доступно)%n",
                    flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                    flight.getId(), flight.getDestination(),
                    flight.getAvailableSeats(), flight.getTotalSeats()));
        }
    }

    private void showFlightInfo() {
        System.out.print("Введіть ID рейсу: ");
        String flightId = scanner.nextLine();
        Optional<Flight> flight = flightController.getFlightInfo(flightId);
        flight.ifPresentOrElse(
                f -> System.out.printf("Рейс: %s до %s, Відправлення: %s, Місця: %d/%d%n",
                        f.getId(), f.getDestination(),
                        f.getDepartureTime().format(DATE_TIME_FORMATTER),
                        f.getAvailableSeats(), f.getTotalSeats()),
                () -> System.out.println("Рейс не знайдено.")
        );
    }

    private void searchAndBookFlight() {
        System.out.print("Введіть пункт призначення: ");
        String destination = scanner.nextLine();
        System.out.print("Введіть дату (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.print("Введіть кількість пасажирів: ");
        int passengers = Integer.parseInt(scanner.nextLine());

        List<Flight> flights = flightController.searchFlights(destination, date.atStartOfDay(), passengers);
        if (flights.isEmpty()) {
            System.out.println("Рейси не знайдено.");
            return;
        }

        System.out.println("\nДоступні рейси:");
        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            System.out.printf("%d. %s до %s (%d/%d місць доступно)%n",
                    i + 1, flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                    flight.getDestination(), flight.getAvailableSeats(), flight.getTotalSeats());
        }
        System.out.print("Виберіть номер рейсу для бронювання (або 0 для виходу): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 0) return;

        Flight selectedFlight = flights.get(choice - 1);
        List<String> passengerNames = new ArrayList<>();
        for (int i = 0; i < passengers; i++) {
            System.out.printf("Введіть ім'я та прізвище пасажира %d: ", i + 1);
            passengerNames.add(scanner.nextLine());
        }

        boolean success = bookingController.book(selectedFlight.getId(), passengerNames);
        System.out.println(success ? "Бронювання успішне!" : "Не вдалося забронювати рейс.");
    }

    private void cancelBooking() {
        System.out.print("Введіть ID бронювання: ");
        String bookingId = scanner.nextLine();
        boolean success = bookingController.cancel(bookingId);
        System.out.println(success ? "Бронювання скасовано." : "Бронювання не знайдено.");
    }

    private void myBookings() {
        System.out.print("Введіть ім'я та прізвище пасажира: ");
        String name = scanner.nextLine();
        List<Booking> bookings = bookingController.getByPassenger(name);
        if (bookings.isEmpty()) {
            System.out.println("Бронювань не знайдено.");
        } else {
            bookings.forEach(System.out::println);
        }
    }
}
