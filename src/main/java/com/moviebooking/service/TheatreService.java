package com.moviebooking.service;

import com.moviebooking.model.Theatre;
import com.moviebooking.repository.TheatreRepository;

import java.util.List;

public class TheatreService {
    private final TheatreRepository theatreRepository;
    private int theatreCounter = 1;

    public TheatreService(TheatreRepository theatreRepository) {
        if (theatreRepository == null){
            throw new IllegalArgumentException("Theatre repository cannot be null.");
        }

        this.theatreRepository = theatreRepository;
    }

    private String generateId(){
        return String.format("THE%03d", theatreCounter++);
    }

    public Theatre addTheatre(String name, int rows, int seatsPerRow){
        String id = generateId();
        Theatre theatre = new Theatre(id, name, rows, seatsPerRow);
        theatreRepository.save(theatre);
        return theatre;
    }

    public Theatre getTheatreById(String id){
        return theatreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Theatre with id " + id + " not found"));
    }

    public List<Theatre> getAllTheatres(){
        return theatreRepository.findAll();
    }

    public void deleteTheatre(String id){
        getTheatreById(id);
        theatreRepository.deleteById(id);
    }
}
