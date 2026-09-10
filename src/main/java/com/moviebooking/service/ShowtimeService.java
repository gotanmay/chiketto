package com.moviebooking.service;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Showtime;
import com.moviebooking.model.Theatre;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.TheatreRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;

    private int showtimeCounter = 1;

    private String generateId() {
        return String.format("SHW%03d", showtimeCounter++);
    }

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository, TheatreRepository theatreRepository) {
        if (showtimeRepository == null) {
            throw new IllegalArgumentException("Showtime Repository cannot be null.");
        }

        if (movieRepository == null) {
            throw new IllegalArgumentException("Movie Repository cannot be null.");
        }

        if (theatreRepository == null) {
            throw new IllegalArgumentException("Theatre Repository cannot be null.");
        }

        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
    }

    public Showtime addShowtime(String movieId, String theatreId, LocalDateTime showTime) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Movie with id " + movieId + " not found"));

        Theatre theatre = theatreRepository.findById(theatreId).orElseThrow(() -> new IllegalArgumentException("Theatre with id " + theatreId + " not found"));

        String id = generateId();
        Showtime showtime = new Showtime(id, movie, theatre, showTime);
        showtimeRepository.save(showtime);
        return showtime;
    }

    public Showtime getShowtimeById(String id) {
        return showtimeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Showtime with id " + id + " not found"));
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public List<Showtime> getShowtimesByMovieId(String movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    public void deleteShowtime(String id) {
        getShowtimeById(id);
        showtimeRepository.deleteById(id);
    }
}
