package com.moviebooking.model;

public class Movie {
    private final String id;
    private final String title;
    private final Genre genre;
    private final int durationMinutes;
    private final String language;
    private final Rating rating;

    public Movie(String id, String title, Genre genre, int durationMinutes, String language, Rating rating) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Movie ID cannot be empty.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie Title cannot be empty.");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Movie Duration cannot be negative.");
        }

        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public Rating getRating() {
        return rating;
    }

    public String getFormattedDuration() {
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return hours + "h " + mins + "m";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | %s | %s | %s", getId(), getTitle(), getGenre(), getFormattedDuration(), getLanguage(), getRating());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
