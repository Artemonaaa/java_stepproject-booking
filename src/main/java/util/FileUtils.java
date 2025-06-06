package util;

import model.Booking;
import java.io.*;
import java.util.List;

public class FileUtils {
    public static void saveBookings(String file, List<Booking> data) {
        try {
            System.out.println("Зберігаємо бронювання в файл: " + new File(file).getAbsolutePath());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Не вдалося зберегти бронювання: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Booking> loadBookings(String file) {
        File f = new File(file);
        System.out.println("Завантажуємо бронювання з файлу: " + f.getAbsolutePath());
        if (!f.exists()) {
            System.out.println("Файл не існує, повертаємо порожній список");
            return List.of();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Не вдалося завантажити бронювання: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
