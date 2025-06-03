package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String destination;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int availableSeats;

    public Flight(String id, String destination, LocalDateTime departureTime, int totalSeats) {
        this.id = id;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public String getId() { return id; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return id.equals(flight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Flight %s to %s at %s (%d/%d seats available)",
                id, destination, departureTime, availableSeats, totalSeats);
    }
}
