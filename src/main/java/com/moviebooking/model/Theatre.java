package com.moviebooking.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Theatre {
    private final String id;
    private final String name;
    private final int rows;
    private final int seatsPerRow;

    public Theatre(String id, String name, int rows, int seatsPerRow) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Theatre ID cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Theatre name cannot be empty");
        }
        if (rows <= 0 || rows > 26) {
            throw new IllegalArgumentException("Rows must be between 1 and 26");
        }
        if (seatsPerRow <= 0 || seatsPerRow > 20) {
            throw new IllegalArgumentException("Seats per row must be between 1 and 20");
        }

        this.id = id;
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int getTotalSeats() {
        return rows * seatsPerRow;
    }

    public Map<String, SeatStatus> generateSeatMap() {
        Map<String, SeatStatus> seatMap = new LinkedHashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                char row = (char) ('A' + i);
                String seatId = "" + row + j;
                seatMap.put(seatId, SeatStatus.AVAILABLE);
            }
        }
        return seatMap;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %d rows x %d seats | Total: %d", getId(), getName(), getRows(), getSeatsPerRow(), getTotalSeats());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Theatre)) return false;
        Theatre theatre = (Theatre) o;
        return id.equals(theatre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
