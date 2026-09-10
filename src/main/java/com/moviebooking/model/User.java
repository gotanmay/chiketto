package com.moviebooking.model;

public class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s", getId(), getName(), getEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
