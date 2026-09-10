package com.moviebooking.model;

import java.util.List;

public class Booking {
    private final String id;
    private final User user;
    private final Showtime showtime;
    private final List<String> bookedSeats;
    private BookingStatus status;

    public Booking(String id, User user, Showtime showtime, List<String> bookedSeats) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be empty");
        }
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime cannot be empty");
        }
        if (bookedSeats == null || bookedSeats.isEmpty()) {
            throw new IllegalArgumentException("Booked seats cannot be empty");
        }

        this.id = id;
        this.user = user;
        this.showtime = showtime;
        this.bookedSeats = bookedSeats;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public int getTotalSeats() {
        return bookedSeats.size();
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        for (String seatId : bookedSeats) {
            showtime.cancelSeat(seatId);
        }
        status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("[%s] User: %s | Show: %s | Seat: %s | Status: %s",
                getId(),
                getUser().getName(),
                getShowtime().getMovie().getTitle(),
                getBookedSeats(),
                getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
