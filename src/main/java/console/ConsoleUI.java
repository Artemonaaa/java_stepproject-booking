package console;

import controller.BookingController;
import dao.BookingDao;
import service.BookingService;
import model.Booking;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final BookingController bookingController;
    private final Scanner scanner;

    public ConsoleUI() {
        this.bookingController = new BookingController(new BookingService(new BookingDao()));
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== ГОЛОВНЕ МЕНЮ ===");
            System.out.println("1. Скасувати бронювання");
            System.out.println("2. Мої рейси");
            System.out.println("3. Створити бронювання (псевдо-рейс)");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> cancelBooking();
                case "2" -> myBookings();
                case "3" -> bookFlight();
                case "0" -> {
                    bookingController.save();
                    System.out.println("До побачення!");
                    return;
                }
                default -> System.out.println("Невідома команда. Спробуйте ще раз.");
            }
        }
    }

    private void cancelBooking() {
        System.out.print("Введіть ID бронювання: ");
        String id = scanner.nextLine();
        boolean success = bookingController.cancel(id);
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

    private void bookFlight() {
        System.out.print("Введіть ID рейсу (умовно): ");
        String flightId = scanner.nextLine();

        System.out.print("Скільки пасажирів?: ");
        int count = Integer.parseInt(scanner.nextLine());

        for (int i = 1; i <= count; i++) {
            System.out.print("Пасажир " + i + " (ім'я та прізвище): ");
            String fullName = scanner.nextLine();
            bookingController.book(flightId, fullName);
        }

        System.out.println("Бронювання створено успішно!");
    }
}

