package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Booking implements Serializable {
    private String id;
    private String flightId;
    private String passengerName;

    public Booking(String flightId, String passengerName) {
        this.id = UUID.randomUUID().toString();
        this.flightId = flightId;
        this.passengerName = passengerName;
    }

    public String getId() { return id; }
    public String getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }

    public void setFlightId(String flightId) { this.flightId = flightId; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

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
        return String.format("Booking{id='%s', flightId='%s', passengerName='%s'}", id, flightId, passengerName);
    }
}
