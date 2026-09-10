package com.moviebooking.service;

import com.moviebooking.model.User;
import com.moviebooking.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    private int userCounter = 1;

    private String generateId() {
        return String.format("USR%08d", userCounter++);
    }

    public UserService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }

        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email) {
        String id = generateId();
        User user = new User(id, name, email);
        userRepository.save(user);
        return user;
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
