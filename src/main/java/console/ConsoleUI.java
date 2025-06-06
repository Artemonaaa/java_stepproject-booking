package console;

import controller.BookingController;
import controller.FlightController;
import exception.BookingNotFoundException;
import exception.InvalidFlightException;
import exception.InvalidInputException;
import model.Flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final BookingController bookingController;
    private final FlightController flightController;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI(BookingController bookingController, FlightController flightController) {
        this.bookingController = bookingController;
        this.flightController = flightController;
    }

    public void start() {
        while (true) {
            try {
                System.out.println("""
                    
===== Авіабронювання =====
                    1. Онлайн-табло
                    2. Переглянути рейс
                    3. Пошук та бронювання рейсу
                    4. Скасувати бронювання
                    5. Мої рейси
                    6. Вихід
                    """);
                System.out.print("Оберіть пункт меню: ");
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> showTimetable();
                    case "2" -> viewFlight();
                    case "3" -> searchAndBookFlight();
                    case "4" -> cancelBooking();
                    case "5" -> viewMyFlights();
                    case "6" -> {
                        bookingController.save();
                        flightController.save();
                        System.out.println("До зустрічі!");
                        return;
                    }
                    default -> System.out.println("❌ Невідомий пункт меню.");
                }
            } catch (Exception e) {
                System.out.println("⚠ Сталася неочікувана помилка: " + e.getMessage());
            }
        }
    }

    private void cancelBooking() {
        try {
            System.out.print("Введіть ID бронювання: ");
            String bookingId = scanner.nextLine();
            if (bookingId.trim().isEmpty()) {
                throw new InvalidInputException("ID бронювання не може бути порожнім");
            }
            boolean success = bookingController.cancel(bookingId);
            System.out.println(success ? "✅ Бронювання скасовано." : "❌ Бронювання не знайдено.");
        } catch (BookingNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Сталася помилка при скасуванні бронювання: " + e.getMessage());
        }
    }

    private void searchAndBookFlight() {
        try {
            System.out.print("Введіть пункт призначення: ");
            String destination = scanner.nextLine();
            if (destination.trim().isEmpty()) {
                throw new InvalidInputException("Пункт призначення не може бути порожнім");
            }

            System.out.print("Введіть дату (yyyy-MM-dd): ");
            LocalDate date;
            try {
                date = LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Неправильний формат дати. Використовуйте формат yyyy-MM-dd");
            }

            System.out.print("Введіть кількість пасажирів: ");
            int passengers;
            try {
                passengers = Integer.parseInt(scanner.nextLine());
                if (passengers <= 0) {
                    throw new InvalidInputException("Кількість пасажирів повинна бути більше 0");
                }
            } catch (NumberFormatException e) {
                throw new InvalidInputException("Неправильний формат кількості пасажирів");
            }

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
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InvalidInputException("Неправильний формат вибору рейсу");
            }

            if (choice == 0) return;

            if (choice < 1 || choice > flights.size()) {
                throw new InvalidInputException("Неправильний вибір рейсу.");
            }

            Flight selectedFlight = flights.get(choice - 1);
            List<String> passengerNames = new ArrayList<>();
            for (int i = 0; i < passengers; i++) {
                System.out.printf("Введіть ім'я та прізвище пасажира %d: ", i + 1);
                String name = scanner.nextLine();
                if (name.trim().isEmpty()) {
                    throw new InvalidInputException("Ім'я пасажира не може бути порожнім");
                }
                passengerNames.add(name);
            }

            boolean success = bookingController.book(selectedFlight.getId(), passengerNames);
            System.out.println(success ? "✅ Бронювання успішне!" : "❌ Не вдалося забронювати рейс.");
        } catch (InvalidFlightException | InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Сталася помилка при бронюванні: " + e.getMessage());
        }
    }

    private void showTimetable() {
        try {
            List<Flight> flights = flightController.getAllFlights();
            if (flights.isEmpty()) {
                System.out.println("Наразі немає доступних рейсів.");
                return;
            }
            System.out.println("\n=== Онлайн-табло ===");
            for (Flight flight : flights) {
                System.out.printf("Рейс %s: %s до %s (%d/%d місць)%n",
                        flight.getId(),
                        flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                        flight.getDestination(),
                        flight.getAvailableSeats(),
                        flight.getTotalSeats());
            }
        } catch (Exception e) {
            System.out.println("⚠ Сталася помилка при отриманні розкладу: " + e.getMessage());
        }
    }

    private void viewFlight() {
        try {
            System.out.print("Введіть ID рейсу: ");
            String flightId = scanner.nextLine();
            if (flightId.trim().isEmpty()) {
                throw new InvalidInputException("ID рейсу не може бути порожнім");
            }
            Flight flight = flightController.getFlight(flightId);
            System.out.printf("\n=== Інформація про рейс %s ===%n", flightId);
            System.out.printf("Пункт призначення: %s%n", flight.getDestination());
            System.out.printf("Час вильоту: %s%n", flight.getDepartureTime().format(DATE_TIME_FORMATTER));
            System.out.printf("Доступно місць: %d/%d%n", flight.getAvailableSeats(), flight.getTotalSeats());
        } catch (InvalidFlightException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Сталася помилка при перегляді рейсу: " + e.getMessage());
        }
    }

    private void viewMyFlights() {
        try {
            System.out.print("Введіть ваше ім'я: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                throw new InvalidInputException("Ім'я не може бути порожнім");
            }
            List<Flight> myFlights = bookingController.getMyFlights(name);
            if (myFlights.isEmpty()) {
                System.out.println("У вас немає заброньованих рейсів.");
                return;
            }
            System.out.println("\n=== Ваші рейси ===");
            for (Flight flight : myFlights) {
                System.out.printf("Рейс %s: %s до %s%n",
                        flight.getId(),
                        flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                        flight.getDestination());
            }
        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Сталася помилка при перегляді ваших рейсів: " + e.getMessage());
        }
    }
}
