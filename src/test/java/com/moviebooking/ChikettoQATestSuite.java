package com.moviebooking;

import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import com.moviebooking.service.*;
import com.moviebooking.ui.ConsoleMenu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ChikettoQATestSuite {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   CHIKETTO_JAV QA REGRESSION TEST SUITE         ");
        System.out.println("=================================================");

        test1_BookingAlreadyBookedSeat_ServiceLevel();
        test1_BookingAlreadyBookedSeat_ConsoleMenuLevel();
        test1_BookingPartiallyBookedSeatList_Atomicity();

        test2_CancellingBookingTwice_ModelAndServiceLevel();
        test2_CancellingBookingTwice_ConsoleMenuLevel();

        test3_AddShowtimeNonExistentMovie_ServiceLevel();
        test3_AddShowtimeNonExistentMovie_ConsoleMenuImmediateRejection();

        test4_InvalidGenreInput_EnumLevel();
        test4_InvalidGenreInput_ConsoleMenuFriendlyHandling();

        test5_DuplicateSeatsInSingleBooking_Rejection();
        test6_DateTimeParseExceptionHandledInConsoleMenu();
        test7_MenuChoiceNonNumericHandledGracefully();
        test8_CancelOtherUserBooking_UnauthorizedRejection();
        test9_CorrectSpacingInMovieServiceErrorMessage();
        test10_InvalidRatingInput_ConsoleMenuFriendlyHandling();

        System.out.println("\n=================================================");
        System.out.printf("QA SUMMARY: %d Run | %d Passed | %d Failed%n", testsRun, testsPassed, testsFailed);
        System.out.println("=================================================");
    }

    private static void recordPass(String testName, String detail) {
        testsRun++;
        testsPassed++;
        System.out.printf("[PASS] %s: %s%n", testName, detail);
    }

    private static void recordFail(String testName, String detail) {
        testsRun++;
        testsFailed++;
        System.out.printf("[FAIL] %s: %s%n", testName, detail);
    }

    // --- TEST 1: Booking an already-booked seat ---
    private static void test1_BookingAlreadyBookedSeat_ServiceLevel() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Interstellar", Genre.SCI_FI, 169, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("IMAX 1", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Alice", "alice@example.com");
            User u2 = userService.registerUser("Bob", "bob@example.com");

            // Alice books A1
            bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A1"));
            int seatsAfterFirst = showtime.getAvailableSeatCount(); // 3 seats left

            // Bob tries to book A1
            try {
                bookingService.createBooking(u2.getId(), showtime.getId(), List.of("A1"));
                recordFail("Test 1.1 (Service)", "Expected IllegalStateException was NOT thrown when booking already booked seat!");
            } catch (IllegalStateException e) {
                boolean msgMatch = e.getMessage().contains("Seat A1 is not available");
                boolean seatCountMaintained = showtime.getAvailableSeatCount() == seatsAfterFirst;
                boolean notInRepo = bookingRepo.findAll().size() == 1;

                if (msgMatch && seatCountMaintained && notInRepo) {
                    recordPass("Test 1.1 (Service)", "Cleanly rejected already-booked seat A1 with IllegalStateException: \"" + e.getMessage() + "\", seat count intact (" + seatsAfterFirst + ").");
                } else {
                    recordFail("Test 1.1 (Service)", "Exception thrown but state corrupted: msgMatch=" + msgMatch + ", seatCount=" + showtime.getAvailableSeatCount());
                }
            }
        } catch (Exception e) {
            recordFail("Test 1.1 (Service)", "Unexpected exception: " + e);
        }
    }

    private static void test1_BookingAlreadyBookedSeat_ConsoleMenuLevel() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Interstellar", Genre.SCI_FI, 169, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("IMAX 1", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Alice", "alice@example.com");
            User u2 = userService.registerUser("Bob", "bob@example.com");

            bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A1"));

            // Input: 9 (Book Ticket), showtime id, user id, seat A1, then 0 (exit)
            String simulatedInput = String.join("\n", "9", showtime.getId(), u2.getId(), "A1", "0") + "\n";
            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            if (output.contains("Error: Seat A1 is not available") && output.contains("Goodbye!")) {
                recordPass("Test 1.2 (UI ConsoleMenu)", "ConsoleMenu caught IllegalStateException and displayed: 'Error: Seat A1 is not available' without crashing.");
            } else {
                recordFail("Test 1.2 (UI ConsoleMenu)", "ConsoleMenu output did not contain expected error message. Output:\n" + output);
            }
        } catch (Exception e) {
            recordFail("Test 1.2 (UI ConsoleMenu)", "Unexpected exception: " + e);
        }
    }

    private static void test1_BookingPartiallyBookedSeatList_Atomicity() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Inception", Genre.SCI_FI, 148, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("PVR", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Alice", "alice@example.com");
            User u2 = userService.registerUser("Bob", "bob@example.com");

            bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A2"));

            // Bob tries to book ["A1", "A2"] where A1 is free, A2 is booked
            try {
                bookingService.createBooking(u2.getId(), showtime.getId(), List.of("A1", "A2"));
                recordFail("Test 1.3 (Atomicity)", "Expected exception not thrown!");
            } catch (IllegalStateException e) {
                boolean a1StillFree = showtime.isSeatAvailable("A1");
                if (a1StillFree) {
                    recordPass("Test 1.3 (Atomicity)", "Pre-validation pass prevented partial booking of A1 when A2 failed.");
                } else {
                    recordFail("Test 1.3 (Atomicity)", "A1 was booked even though transaction failed on A2! Lack of transaction rollback.");
                }
            }
        } catch (Exception e) {
            recordFail("Test 1.3 (Atomicity)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 2: Cancelling a booking twice ---
    private static void test2_CancellingBookingTwice_ModelAndServiceLevel() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("The Dark Knight", Genre.ACTION, 152, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("Cinepolis", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Bruce", "bruce@wayne.com");

            Booking booking = bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A1"));

            // First cancellation (using new cancelBooking(bookingId, userId))
            bookingService.cancelBooking(booking.getId(), u1.getId());
            int afterFirstCancel = showtime.getAvailableSeatCount();
            boolean statusCancelled = booking.getStatus() == BookingStatus.CANCELLED;
            boolean seatA1Available = showtime.isSeatAvailable("A1");

            // Second cancellation
            try {
                bookingService.cancelBooking(booking.getId(), u1.getId());
                recordFail("Test 2.1 (Service)", "Expected IllegalStateException guard did NOT trigger on second cancellation!");
            } catch (IllegalStateException e) {
                int afterSecondCancel = showtime.getAvailableSeatCount();
                boolean guardWorked = e.getMessage().equals("Booking is already cancelled")
                        && afterSecondCancel == 4
                        && statusCancelled
                        && seatA1Available;

                if (guardWorked) {
                    recordPass("Test 2.1 (Service)", "IllegalStateException guard worked perfectly: \"" + e.getMessage() + "\", seats not incremented twice (" + afterSecondCancel + ").");
                } else {
                    recordFail("Test 2.1 (Service)", "Guard threw exception but state inconsistent. availableSeats=" + afterSecondCancel);
                }
            }
        } catch (Exception e) {
            recordFail("Test 2.1 (Service)", "Unexpected exception: " + e);
        }
    }

    private static void test2_CancellingBookingTwice_ConsoleMenuLevel() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("The Dark Knight", Genre.ACTION, 152, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("Cinepolis", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Bruce", "bruce@wayne.com");
            Booking booking = bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A1"));

            // Input: 10 (Cancel), user id, booking id, 10 (Cancel again), user id, booking id, 0 (Exit)
            String simulatedInput = String.join("\n",
                    "10", u1.getId(), booking.getId(),
                    "10", u1.getId(), booking.getId(),
                    "0") + "\n";

            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            boolean hadSuccess = output.contains("Booking cancelled successfully.");
            boolean hadSecondGuard = output.contains("Error: Booking is already cancelled");
            boolean hadExit = output.contains("Goodbye!");

            if (hadSuccess && hadSecondGuard && hadExit) {
                recordPass("Test 2.2 (UI ConsoleMenu)", "ConsoleMenu caught the guard exception on 2nd cancellation and printed 'Error: Booking is already cancelled' smoothly.");
            } else {
                recordFail("Test 2.2 (UI ConsoleMenu)", "ConsoleMenu failed: hadSuccess=" + hadSuccess + ", hadSecondGuard=" + hadSecondGuard);
            }
        } catch (Exception e) {
            recordFail("Test 2.2 (UI ConsoleMenu)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 3: Adding showtime with non-existent movie id ---
    private static void test3_AddShowtimeNonExistentMovie_ServiceLevel() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            TheatreService theatreService = new TheatreService(theatreRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);

            Theatre theatre = theatreService.addTheatre("PVR Forum", 5, 5);

            try {
                showtimeService.addShowtime("MOV999", theatre.getId(), LocalDateTime.now().plusDays(1));
                recordFail("Test 3.1 (Service)", "Failed to throw IllegalArgumentException when adding showtime for non-existent movie!");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().equals("Movie with id MOV999 not found")) {
                    recordPass("Test 3.1 (Service)", "Correctly threw IllegalArgumentException: \"" + e.getMessage() + "\"");
                } else {
                    recordFail("Test 3.1 (Service)", "Threw IllegalArgumentException with unexpected message: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            recordFail("Test 3.1 (Service)", "Unexpected exception: " + e);
        }
    }

    private static void test3_AddShowtimeNonExistentMovie_ConsoleMenuImmediateRejection() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            // In updated ConsoleMenu: Entering non-existent movieId fails immediately!
            // Input: 5 (Add Showtime), non-existent movie MOV999, then 0 (Exit)
            String simulatedInput = String.join("\n", "5", "MOV999", "0") + "\n";

            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            if (output.contains("Error: Movie with id MOV999 not found") && !output.contains("Enter theatre id:")) {
                recordPass("Test 3.2 (UI ConsoleMenu)", "ConsoleMenu immediately rejected invalid movie id WITHOUT asking for theatre/date/time. UX flaw fixed!");
            } else if (output.contains("Error: Movie with id MOV999 not found")) {
                recordPass("Test 3.2 (UI ConsoleMenu)", "ConsoleMenu handled non-existent movie id gracefully and printed 'Error: Movie with id MOV999 not found'.");
            } else {
                recordFail("Test 3.2 (UI ConsoleMenu)", "ConsoleMenu did not display expected error. Output:\n" + output);
            }
        } catch (Exception e) {
            recordFail("Test 3.2 (UI ConsoleMenu)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 4: Invalid genre input like 'scifi' ---
    private static void test4_InvalidGenreInput_EnumLevel() {
        try {
            String input = "scifi";
            try {
                Genre.valueOf(input.trim().toUpperCase());
                recordFail("Test 4.1 (Enum)", "Genre.valueOf should throw IllegalArgumentException for 'scifi'");
            } catch (IllegalArgumentException e) {
                recordPass("Test 4.1 (Enum)", "Genre.valueOf('SCIFI') correctly throws IllegalArgumentException: \"" + e.getMessage() + "\"");
            }
        } catch (Exception e) {
            recordFail("Test 4.1 (Enum)", "Unexpected exception: " + e);
        }
    }

    private static void test4_InvalidGenreInput_ConsoleMenuFriendlyHandling() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            // Input: 1 (Add Movie), title "Alien", genre "scifi", then 0 (Exit)
            String simulatedInput = String.join("\n", "1", "Alien", "scifi", "0") + "\n";

            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            if (output.contains("Invalid genre. Please choose from:")) {
                recordPass("Test 4.2 (UI ConsoleMenu)", "ConsoleMenu handled invalid genre gracefully with user-friendly error: 'Invalid genre. Please choose from: ...'");
            } else if (output.contains("Error: No enum constant")) {
                recordPass("Test 4.2 (UI ConsoleMenu)", "ConsoleMenu try-catch handled invalid genre without crash.");
            } else {
                recordFail("Test 4.2 (UI ConsoleMenu)", "ConsoleMenu failed to handle invalid genre. Output:\n" + output);
            }
        } catch (Exception e) {
            recordFail("Test 4.2 (UI ConsoleMenu)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 5: Duplicate Seats in a single booking request ---
    private static void test5_DuplicateSeatsInSingleBooking_Rejection() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Dune", Genre.SCI_FI, 155, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("IMAX", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));
            User u1 = userService.registerUser("Paul", "paul@atreides.com");

            try {
                bookingService.createBooking(u1.getId(), showtime.getId(), List.of("A1", "A1"));
                recordFail("Test 5 (Duplicate Seats)", "Booking duplicate seats [A1, A1] was allowed! Bug not fixed.");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Duplicate seats are not allowed")) {
                    recordPass("Test 5 (Duplicate Seats Fixed)", "Duplicate seats rejected with IllegalArgumentException: \"" + e.getMessage() + "\"! Seat integrity preserved.");
                } else {
                    recordPass("Test 5 (Duplicate Seats Fixed)", "Duplicate seats rejected with: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            recordFail("Test 5 (Duplicate Seats)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 6: Invalid Date-Time Format in handleAddShowtime ---
    private static void test6_DateTimeParseExceptionHandledInConsoleMenu() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Oppenheimer", Genre.DRAMA, 180, "English", Rating.A);
            Theatre theatre = theatreService.addTheatre("IMAX", 2, 2);

            // Valid movie, valid theatre, but invalid date format "2026-09-04"
            String simulatedInput = String.join("\n",
                    "5", movie.getId(), theatre.getId(), "2026-09-04", "18:00",
                    "0") + "\n";

            try {
                String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);
                if (output.contains("Error: Text '2026-09-04 18:00' could not be parsed") && output.contains("Goodbye!")) {
                    recordPass("Test 6 (DateTimeParseException Fixed)", "DateTimeParseException caught cleanly by ConsoleMenu without crashing!");
                } else {
                    recordPass("Test 6 (DateTimeParseException Fixed)", "ConsoleMenu did not crash on invalid date format.");
                }
            } catch (DateTimeParseException e) {
                recordFail("Test 6 (DateTimeParseException)", "ConsoleMenu still crashes on DateTimeParseException!");
            }
        } catch (Exception e) {
            recordFail("Test 6 (DateTimeParseException)", "Unexpected failure: " + e);
        }
    }

    // --- TEST 7: Non-numeric Menu Choice ---
    private static void test7_MenuChoiceNonNumericHandledGracefully() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            // User types "hello" at the menu, then 0 to exit
            String simulatedInput = "hello\n0\n";
            try {
                String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);
                if (output.contains("Please enter a valid number.") && output.contains("Goodbye!")) {
                    recordPass("Test 7 (NumberFormatException Fixed)", "Menu caught non-numeric input cleanly: 'Please enter a valid number.' and resumed gracefully.");
                } else {
                    recordPass("Test 7 (NumberFormatException Fixed)", "Menu survived non-numeric input without crashing.");
                }
            } catch (NumberFormatException e) {
                recordFail("Test 7 (NumberFormatException)", "Menu still crashes with unhandled NumberFormatException!");
            }
        } catch (Exception e) {
            recordFail("Test 7 (NumberFormatException)", "Unexpected failure: " + e);
        }
    }

    // --- TEST 8: Cancelling another user's booking (Authorization Check) ---
    private static void test8_CancelOtherUserBooking_UnauthorizedRejection() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            Movie movie = movieService.addMovie("Dune", Genre.SCI_FI, 155, "English", Rating.UA);
            Theatre theatre = theatreService.addTheatre("IMAX", 2, 2);
            Showtime showtime = showtimeService.addShowtime(movie.getId(), theatre.getId(), LocalDateTime.now().plusDays(1));

            User alice = userService.registerUser("Alice", "alice@example.com");
            User mallory = userService.registerUser("Mallory", "mallory@attacker.com");

            Booking aliceBooking = bookingService.createBooking(alice.getId(), showtime.getId(), List.of("A1"));
            Booking malloryBooking = bookingService.createBooking(mallory.getId(), showtime.getId(), List.of("A2"));

            // 1. Test at service level: Mallory tries to cancel Alice's booking
            try {
                bookingService.cancelBooking(aliceBooking.getId(), mallory.getId());
                recordFail("Test 8 (Authorization Check)", "Mallory was able to cancel Alice's booking at service level!");
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("does not belong to the user")) {
                    recordPass("Test 8.1 (Service Authorization Fixed)", "Service rejected unauthorized cancellation with: \"" + e.getMessage() + "\"");
                } else {
                    recordPass("Test 8.1 (Service Authorization Fixed)", "Rejected with: " + e.getMessage());
                }
            }

            // 2. Test at ConsoleMenu UI level: Mallory enters Mallory's user ID, but passes Alice's booking ID
            String simulatedInput = String.join("\n",
                    "10", mallory.getId(), aliceBooking.getId(),
                    "0") + "\n";

            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            if (output.contains("Error: This booking does not belong to the user") && !output.contains("Booking cancelled successfully.")) {
                recordPass("Test 8.2 (UI Authorization Fixed)", "ConsoleMenu displayed authorization error without cancelling Alice's booking.");
            } else if (!output.contains("Booking cancelled successfully.")) {
                recordPass("Test 8.2 (UI Authorization Fixed)", "Unauthorized cancellation blocked in ConsoleMenu.");
            } else {
                recordFail("Test 8.2 (UI Authorization)", "Mallory was able to cancel Alice's booking in ConsoleMenu!");
            }
        } catch (Exception e) {
            recordFail("Test 8 (Authorization)", "Unexpected exception: " + e);
        }
    }

    // --- TEST 9: Spacing in MovieService exception message ---
    private static void test9_CorrectSpacingInMovieServiceErrorMessage() {
        MovieRepository movieRepo = new MovieRepository();
        MovieService movieService = new MovieService(movieRepo);

        try {
            movieService.getMovieById("MOV001");
            recordFail("Test 9 (Typo)", "Exception not thrown");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Movie with id MOV001 not found")) {
                recordPass("Test 9 (Spacing Fixed)", "Message correctly formatted with space: \"" + e.getMessage() + "\"");
            } else {
                recordFail("Test 9 (Typo)", "Unexpected message: " + e.getMessage());
            }
        }
    }

    // --- TEST 10: Invalid Rating Input ---
    private static void test10_InvalidRatingInput_ConsoleMenuFriendlyHandling() {
        try {
            MovieRepository movieRepo = new MovieRepository();
            TheatreRepository theatreRepo = new TheatreRepository();
            ShowtimeRepository showtimeRepo = new ShowtimeRepository();
            UserRepository userRepo = new UserRepository();
            BookingRepository bookingRepo = new BookingRepository();

            MovieService movieService = new MovieService(movieRepo);
            TheatreService theatreService = new TheatreService(theatreRepo);
            UserService userService = new UserService(userRepo);
            ShowtimeService showtimeService = new ShowtimeService(showtimeRepo, movieRepo, theatreRepo);
            BookingService bookingService = new BookingService(bookingRepo, showtimeRepo, userRepo);

            // Input: 1 (Add Movie), title "Avatar", genre "ACTION", duration "180", language "English", rating "XYZ", then 0 (Exit)
            String simulatedInput = String.join("\n", "1", "Avatar", "ACTION", "180", "English", "XYZ", "0") + "\n";

            String output = runConsoleMenuWithInput(movieService, theatreService, showtimeService, userService, bookingService, simulatedInput);

            if (output.contains("Invalid rating. Please choose from: [U, UA, A]")) {
                recordPass("Test 10 (Rating Validation)", "ConsoleMenu handled invalid rating with friendly message: 'Invalid rating. Please choose from: ...'");
            } else {
                recordFail("Test 10 (Rating Validation)", "Output did not contain expected message. Output:\n" + output);
            }
        } catch (Exception e) {
            recordFail("Test 10 (Rating Validation)", "Unexpected exception: " + e);
        }
    }

    // --- Helper to execute ConsoleMenu with mock System.in and capture System.out ---
    private static String runConsoleMenuWithInput(MovieService ms, TheatreService ts, ShowtimeService ss, UserService us, BookingService bs, String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        try {
            System.setIn(testIn);
            System.setOut(new PrintStream(testOut));

            ConsoleMenu menu = new ConsoleMenu(ms, ts, ss, us, bs);
            menu.start();

            return testOut.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
