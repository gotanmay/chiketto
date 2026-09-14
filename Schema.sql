CREATE TABLE movies (
                        id               VARCHAR(10) PRIMARY KEY,
                        title            VARCHAR(255) NOT NULL,
                        genre            VARCHAR(20) NOT NULL,
                        duration_minutes INT NOT NULL,
                        language         VARCHAR(50) NOT NULL,
                        rating           VARCHAR(10) NOT NULL
);

CREATE TABLE theatres (
                          id             VARCHAR(10) PRIMARY KEY,
                          name           VARCHAR(255) NOT NULL,
                          rows           INT NOT NULL,
                          seats_per_row  INT NOT NULL
);

CREATE TABLE showtimes (
                           id          VARCHAR(10) PRIMARY KEY,
                           movie_id    VARCHAR(10) NOT NULL REFERENCES movies(id) ON DELETE RESTRICT,
                           theatre_id  VARCHAR(10) NOT NULL REFERENCES theatres(id) ON DELETE RESTRICT,
                           show_time   TIMESTAMP NOT NULL
);

CREATE TABLE seats (
                       showtime_id  VARCHAR(10) REFERENCES showtimes(id) ON DELETE CASCADE,
                       seat_number  VARCHAR(5) NOT NULL,
                       status       VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                       PRIMARY KEY (showtime_id, seat_number)
);

CREATE TABLE users (
                       id     VARCHAR(15) PRIMARY KEY,
                       name   VARCHAR(255) NOT NULL,
                       email  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE bookings (
                          id           VARCHAR(10) PRIMARY KEY,
                          user_id      VARCHAR(15) NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                          showtime_id  VARCHAR(10) NOT NULL REFERENCES showtimes(id) ON DELETE RESTRICT,
                          status       VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
);

CREATE TABLE booking_seats (
                               booking_id   VARCHAR(10) REFERENCES bookings(id) ON DELETE CASCADE,
                               showtime_id  VARCHAR(10) NOT NULL,
                               seat_number  VARCHAR(5) NOT NULL,
                               PRIMARY KEY (booking_id, seat_number),
                               FOREIGN KEY (showtime_id, seat_number) REFERENCES seats(showtime_id, seat_number)
);