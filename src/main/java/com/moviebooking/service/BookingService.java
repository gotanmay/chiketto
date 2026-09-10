package com.moviebooking.service;

import com.moviebooking.model.Booking;
import com.moviebooking.model.Showtime;
import com.moviebooking.model.User;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.UserRepository;

import java.util.List;
import java.util.Set;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    private int bookingCounter = 1;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository, UserRepository userRepository) {
        if (bookingRepository == null) {
            throw new IllegalArgumentException("Booking Repository cannot be null.");
        }

        if (showtimeRepository == null) {
            throw new IllegalArgumentException("Showtime Repository cannot be null.");
        }

        if (userRepository == null) {
            throw new IllegalArgumentException("User Repository cannot be null.");
        }

        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
    }

    private String generateId() {
        return String.format("BKG%03d", bookingCounter++);
    }

    public Booking createBooking(String userId, String showtimeId, List<String> requestedSeats) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new IllegalArgumentException("Showtime with id " + showtimeId + " not found"));

        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new IllegalArgumentException("Requested seats cannot be empty");
        }

        if (requestedSeats.size() != Set.copyOf(requestedSeats).size()) {
            throw new IllegalArgumentException("Duplicate seats are not allowed in a single booking");
        }

        for (String seatId : requestedSeats) {
            if (!showtime.isSeatAvailable(seatId)) {
                throw new IllegalStateException("Seat " + seatId + " is not available");
            }
        }

        for (String seatId : requestedSeats) {
            showtime.bookSeat(seatId);
        }

        String id = generateId();
        Booking booking = new Booking(id, user, showtime, requestedSeats);
        bookingRepository.save(booking);
        return booking;
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking with id " + id + " not found"));
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public void cancelBooking(String bookingId, String userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getUser().getId().equals(userId)){
            throw new IllegalStateException("This booking does not belong to the user" + userId);
        }

        booking.cancel();
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
