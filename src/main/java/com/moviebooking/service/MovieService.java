package com.moviebooking.service;

import com.moviebooking.model.Genre;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Rating;
import com.moviebooking.repository.MovieRepository;

import java.util.List;

public class MovieService {
    private final MovieRepository movieRepository;
    private int movieCounter = 1;

    public MovieService(MovieRepository movieRepository) {
        if (movieRepository == null) {
            throw new IllegalArgumentException("Movie Repository cannot be null.");
        }
        this.movieRepository = movieRepository;
    }

    private String generateId() {
        return String.format("MOV%03d", movieCounter++);
    }

    public Movie addMovie(String title, Genre genre, int durationMinutes, String language, Rating rating) {
        String id = generateId();
        Movie movie = new Movie(id, title, genre, durationMinutes, language, rating);
        movieRepository.save(movie);
        return movie;
    }

    public Movie getMovieById(String id) {
        return movieRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Movie with id " + id + " not found"));
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public void deleteMovie(String id) {
        getMovieById(id);
        movieRepository.deleteById(id);
    }
}