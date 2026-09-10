package com.moviebooking.repository;

import com.moviebooking.model.Showtime;

import java.util.*;

public class ShowtimeRepository {
    private final Map<String, Showtime> showtimes = new HashMap<>();

    public void save(Showtime showtime) {
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime cannot be null.");
        }
        showtimes.put(showtime.getId(), showtime);
    }

    public Optional<Showtime> findById(String id) {
        return Optional.ofNullable(showtimes.get(id));
    }

    public List<Showtime> findAll() {
        return new ArrayList<>(showtimes.values());
    }

    public void deleteById(String id) {
        if (!showtimes.containsKey(id)) {
            throw new IllegalArgumentException("Showtime with id " + id + " does not exist.");
        }
        showtimes.remove(id);
    }

    public List<Showtime> findByMovieId(String movieId) {
        List<Showtime> result = new ArrayList<>();

        for (Showtime s : showtimes.values()) {
            if (s.getMovie().getId().equals(movieId)) {
                result.add(s);
            }
        }
        return result;
    }
}
