package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Booking implements Serializable {
    private final String id;
    private final String flightId;
    private final List<String> passengerNames;

    public Booking(String flightId, List<String> passengerNames) {
        this.id = UUID.randomUUID().toString();
        this.flightId = flightId;
        this.passengerNames = passengerNames;
    }

    public String getId() {
        return id;
    }

    public String getFlightId() {
        return flightId;
    }

    public List<String> getPassengerNames() {
        return passengerNames;
    }

    public void setFlightId(String flightId) {
        throw new UnsupportedOperationException("Flight ID cannot be changed.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Booking{id='%s', flightId='%s', passengerNames=%s}", id, flightId, passengerNames);
    }
}
