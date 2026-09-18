package com.moviebooking.repository;

import com.moviebooking.db.DBConnection;
import com.moviebooking.model.Genre;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MovieRepository {
    private final Map<String, Movie> movies = new HashMap<>();

    public void save(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null.");
        }

        String sql = "INSERT INTO movies (id, title, genre, duration_minutes, language, rating) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getId());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getGenre().name());
            stmt.setInt(4, movie.getDurationMinutes());
            stmt.setString(5, movie.getLanguage());
            stmt.setString(6, movie.getRating().name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save movie: " + e.getMessage(), e);
        }
    }

    public Optional<Movie> findById(String id) {
        String sql = "SELECT * FROM movies WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                Genre genre = Genre.valueOf(rs.getString("genre"));
                int durationMinutes = rs.getInt("duration_minutes");
                String language = rs.getString("language");
                Rating rating = Rating.valueOf(rs.getString("rating"));

                Movie movie = new Movie(id, title, genre, durationMinutes, language, rating);
                return Optional.of(movie);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find movie: " + e.getMessage(), e);
        }
    }

    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    public void deleteById(String id) {
        if (!movies.containsKey(id)) {
            throw new IllegalArgumentException("Movie with id " + id + " not found.");
        }
        movies.remove(id);
    }
}
