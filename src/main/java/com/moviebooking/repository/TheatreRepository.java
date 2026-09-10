package com.moviebooking.repository;

import com.moviebooking.model.Theatre;

import java.util.*;

public class TheatreRepository {
    private final Map<String, Theatre> theatres = new HashMap<>();

    public void save(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre id cannot be null.");
        }
        theatres.put(theatre.getId(), theatre);
    }

    public Optional<Theatre> findById(String id) {
        return Optional.ofNullable(theatres.get(id));
    }

    public List<Theatre> findAll() {
        return new ArrayList<>(theatres.values());
    }

    public void deleteById(String id) {
        if (!theatres.containsKey(id)) {
            throw new IllegalArgumentException("Theatre with id " + id + " does not exist.");
        }
        theatres.remove(id);
    }
}
