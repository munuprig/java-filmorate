package ru.yandex.practicum.filmorate.exception;

public class ReviewsNotFoundException extends RuntimeException {
    public ReviewsNotFoundException(String message) {
        super(message);
    }
}
