  # Chiketto [Movie Ticket Booking System]

A Java based movie ticket booking system built to showcase backend
engineering fundamentals. Currently a CLI application backed by
PostgreSQL, evolving toward a Spring Boot REST API.

## Features
- Add/view movies, theatres, and showtimes
- Real-time seat availability tracking
- Book and cancel tickets with atomic seat validation
- User registration and booking history

## Tech Stack
- Java 24
- PostgreSQL + JDBC
- Maven

## Architecture
Layered design — Model / Repository / Service / Console UI —
built to transition cleanly into Spring Boot's Controller /
Service / Repository pattern in a later phase.

## Running Locally
1. Create a PostgreSQL database named `moviebooking`
2. Copy `db.properties.example` to `db.properties` and fill in your credentials
3. Run the SQL schema (see /schema.sql)
4. Run `Main.java`
