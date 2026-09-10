package com.moviebooking.repository;

import com.moviebooking.model.User;

import java.util.*;

public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can not be null");
        }
        users.put(user.getId(), user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(String id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with id:" + id + " not found.");
        }
        users.remove(id);
    }
}
