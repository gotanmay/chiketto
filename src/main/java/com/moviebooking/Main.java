package com.moviebooking;

import com.moviebooking.db.DBConnection;
import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import com.moviebooking.service.*;
import com.moviebooking.ui.ConsoleMenu;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        /*MovieRepository movieRepo = new MovieRepository();
        TheatreRepository theatreRepo = new TheatreRepository();
        ShowtimeRepository showtimeRepo = new ShowtimeRepository();
        UserRepository userRepo = new UserRepository();
        BookingRepository bookingRepo = new BookingRepository();

        MovieService movieService = new MovieService(movieRepo);
        TheatreService theatreService = new TheatreService(theatreRepo);
        UserService userService = new UserService(userRepo);
        ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
        BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

        ConsoleMenu menu = new ConsoleMenu(movieService, theatreService, showtimeService, userService, bookingService);
        menu.start();

         */

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}