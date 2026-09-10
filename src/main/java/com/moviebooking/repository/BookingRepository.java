package com.moviebooking.repository;

import com.moviebooking.model.Booking;

import java.util.*;

public class BookingRepository {
    private final Map<String, Booking> bookings = new HashMap<>();

    public void save(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        bookings.put(booking.getId(), booking);
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public void deleteById(String id) {
        if (!bookings.containsKey(id)) {
            throw new IllegalArgumentException("Booking with id " + id + " does not exist");
        }
        bookings.remove(id);
    }

    public List<Booking> findByUserId(String userId) {
        List<Booking> result = new ArrayList<>();

        for (Booking b : bookings.values()) {
            if (b.getUser().getId().equals(userId)) {
                result.add(b);
            }
        }
        return result;
    }
}
