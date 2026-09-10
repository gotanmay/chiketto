package com.moviebooking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Showtime {
    private final String id;
    private final Movie movie;
    private final Theatre theatre;
    private final LocalDateTime showTime;
    private final Map<String, SeatStatus> seats;
    private int availableSeatCount;

    public Showtime(String id, Movie movie, Theatre theatre, LocalDateTime showTime) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Showtime ID cannot be empty.");
        }
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null.");
        }
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre cannot be null.");
        }
        if (showTime == null) {
            throw new IllegalArgumentException("Showtime cannot be null.");
        }

        this.id = id;
        this.movie = movie;
        this.theatre = theatre;
        this.showTime = showTime;

        this.seats = theatre.generateSeatMap();
        this.availableSeatCount = seats.size();
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public Map<String, SeatStatus> getSeats() {
        return seats;
    }

    public int getTotalSeats() {
        return seats.size();
    }

    public boolean isSeatAvailable(String seatID) {
        if (!seats.containsKey(seatID)) {
            throw new IllegalArgumentException("Seat " + seatID + " does not exist.");
        }
        return seats.get(seatID) == SeatStatus.AVAILABLE;
    }

    public int getAvailableSeatCount() {
        return availableSeatCount;
    }

    public boolean bookSeat(String seatID) {
        if (!isSeatAvailable(seatID)) {
            return false;
        }
        seats.put(seatID, SeatStatus.BOOKED);
        availableSeatCount--;
        return true;
    }

    public void cancelSeat(String seatID) {
        if (!seats.containsKey(seatID)) {
            throw new IllegalArgumentException("Seat " + seatID + " does not exist.");
        }
        seats.put(seatID, SeatStatus.AVAILABLE);
        availableSeatCount++;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Seats Available: %d",
                getId(),
                getMovie().getTitle(),
                getTheatre().getName(),
                getShowTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                getAvailableSeatCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Showtime)) return false;
        Showtime showtime = (Showtime) o;
        return id.equals(showtime.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
