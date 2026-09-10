package com.moviebooking.ui;

import com.moviebooking.model.*;
import com.moviebooking.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final MovieService movieService;
    private final TheatreService theatreService;
    private final ShowtimeService showtimeService;
    private final UserService userService;
    private final BookingService bookingService;
    private final Scanner scanner;

    public ConsoleMenu(MovieService movieService, TheatreService theatreService, ShowtimeService showtimeService, UserService userService, BookingService bookingService) {
        this.movieService = movieService;
        this.theatreService = theatreService;
        this.showtimeService = showtimeService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Movie Booking System");
        boolean running = true;
        while (running) {
            printMenu();
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            switch (choice) {
                case 1 -> handleAddMovie();
                case 2 -> handleViewMovies();
                case 3 -> handleAddTheatres();
                case 4 -> handleViewTheatres();
                case 5 -> handleAddShowtime();
                case 6 -> handleViewShowtimes();
                case 7 -> handleViewShowtimesByMovie();
                case 8 -> handleRegisterUser();
                case 9 -> handleBookTicket();
                case 10 -> handleCancelBooking();
                case 11 -> handleViewMyBookings();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n========== MOVIE BOOKING SYSTEM ==========");
        System.out.println("1. Add Movie");
        System.out.println("2. View All Movies");
        System.out.println("3. Add Theatre");
        System.out.println("4. View All Theatres");
        System.out.println("5. Add Showtime");
        System.out.println("6. View All Showtimes");
        System.out.println("7. View Showtimes By Movie");
        System.out.println("8. Register User");
        System.out.println("9. Book Ticket");
        System.out.println("10. Cancel Booking");
        System.out.println("11. View My Bookings");
        System.out.println("0. Exit");
        System.out.println("Enter choice:");
    }

    private void handleAddMovie() {
        try {
            System.out.println("Enter title: ");
            String title = scanner.nextLine().trim();

            Genre genre;
            System.out.println("Available genres: " + Arrays.toString(Genre.values()));
            System.out.print("Enter genre: ");
            try {
                genre = Genre.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid genre. Please choose from: " + Arrays.toString(Genre.values()));
                return;
            }

            System.out.print("Enter duration (minutes): ");
            int duration = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter language: ");
            String language = scanner.nextLine().trim();

            Rating rating;
            System.out.println("Available ratings: " + Arrays.toString(Rating.values()));
            System.out.print("Enter rating: ");
            try {
                rating = Rating.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid rating. Please choose from: " + Arrays.toString(Rating.values()));
                return;
            }

            Movie movie = movieService.addMovie(title, genre, duration, language, rating);
            System.out.println("Movie added successfully: " + movie);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewMovies() {
        List<Movie> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies available.");
        } else {
            System.out.println("\n--- All Movies ---");
            movies.forEach(System.out::println);
        }
    }

    private void handleAddTheatres() {
        try {
            System.out.print("Enter theatre name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter number of rows: ");
            int rows = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter seats per row: ");
            int seatsPerRow = Integer.parseInt(scanner.nextLine().trim());

            Theatre theatre = theatreService.addTheatre(name, rows, seatsPerRow);
            System.out.println("Theatre added successfully: " + theatre);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewTheatres() {
        List<Theatre> theatres = theatreService.getAllTheatres();
        if (theatres.isEmpty()) {
            System.out.println("No theatres available.");
        } else {
            System.out.println("\n--- All Theatres ---");
            theatres.forEach(System.out::println);
        }
    }

    private void handleAddShowtime() {
        try {
            handleViewMovies();
            System.out.print("Enter movie id: ");
            String movieId = scanner.nextLine().trim();
            Movie movie = movieService.getMovieById(movieId);

            handleViewTheatres();
            System.out.print("Enter theatre id: ");
            String theatreId = scanner.nextLine().trim();
            Theatre theatre = theatreService.getTheatreById(theatreId);

            System.out.print("Enter date (dd-MM-yyyy): ");
            String date = scanner.nextLine().trim();

            System.out.print("Enter time (HH:mm): ");
            String time = scanner.nextLine().trim();

            LocalDateTime showTime = LocalDateTime.parse(
                    date + " " + time,
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            );

            Showtime showtime = showtimeService.addShowtime(movieId, theatreId, showTime);
            System.out.println("Showtime added successfully: " + showtime);

        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        if (showtimes.isEmpty()) {
            System.out.println("No showtimes available.");
        } else {
            System.out.println("\n--- All Showtimes ---");
            showtimes.forEach(System.out::println);
        }
    }

    private void handleViewShowtimesByMovie() {
        try {
            handleViewMovies();
            System.out.print("Enter movie id: ");
            String movieId = scanner.nextLine().trim();

            List<Showtime> showtimes = showtimeService.getShowtimesByMovieId(movieId);
            if (showtimes.isEmpty()) {
                System.out.println("No showtimes found for this movie.");
            } else {
                System.out.println("\n--- Showtimes ---");
                showtimes.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleRegisterUser() {
        try {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            User user = userService.registerUser(name, email);
            System.out.println("User registered successfully: " + user);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleBookTicket() {
        try {
            handleViewShowtimes();
            System.out.print("Enter showtime id: ");
            String showtimeId = scanner.nextLine().trim();

            Showtime showtime = showtimeService.getShowtimeById(showtimeId);
            System.out.println("\n--- Available Seats ---");
            showtime.getSeats().forEach((seatId, status) -> {
                if (status == SeatStatus.AVAILABLE) {
                    System.out.println(seatId + " ");
                }
            });
            System.out.println();

            System.out.print("Enter your user id: ");
            String userId = scanner.nextLine().trim();

            System.out.print("Enter seats separated by comma (e.g. A1,A2): ");
            String seatInput = scanner.nextLine().trim();
            List<String> requestedSeats = Arrays.stream(seatInput.split(",")).map(String::trim).toList();

            Booking booking = bookingService.createBooking(userId, showtimeId, requestedSeats);
            System.out.println("Booking confirmed: " + booking);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleCancelBooking() {
        try {
            System.out.print("Enter your user id: ");
            String userId = scanner.nextLine().trim();

            List<Booking> bookings = bookingService.getBookingsByUserId(userId);
            if (bookings.isEmpty()) {
                System.out.println("No booking found for this user.");
                return;
            }
            System.out.println("\n--- YourBookings ---");
            bookings.forEach(System.out::println);

            System.out.print("Enter booking id to cancel: ");
            String bookingId = scanner.nextLine().trim();

            bookingService.cancelBooking(bookingId, userId);
            System.out.println("Booking cancelled successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewMyBookings() {
        try {
            System.out.print("Enter your user id: ");
            String userId = scanner.nextLine().trim();

            List<Booking> bookings = bookingService.getBookingsByUserId(userId);
            if (bookings.isEmpty()) {
                System.out.println("No bookings found.");
            } else {
                System.out.println("\n--- Your Bookings ---");
                bookings.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
