package util;

import model.Booking;
import java.io.*;
import java.util.List;

public class FileUtils {
    public static void saveBookings(String file, List<Booking> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Не вдалося зберегти бронювання: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookings(String file) {
        File f = new File(file);
        if (!f.exists()) return List.of();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Не вдалося завантажити бронювання: " + e.getMessage());
            return List.of();
        }
    }
}
