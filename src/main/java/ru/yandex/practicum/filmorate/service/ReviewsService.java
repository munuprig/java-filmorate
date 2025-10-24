package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;
    private final FeedStorage feedStorage;

    public List<Review> findAllReviews() {
        return reviewsStorage.findAll().stream()
                .sorted((r1, r2) -> r2.getReviewId().compareTo(r1.getReviewId()))
                .collect(Collectors.toList());
    }

    public Review findById(Long id) {
        if (reviewsStorage.findReviewById(id) != null) {
            return reviewsStorage.findReviewById(id);
        }
        throw new ReviewsNotFoundException("Отзыв не найден с id = " + id);
    }

    public Review createReview(Review review) {
        if (review.getUserId() <= 0L || review.getFilmId() <= 0L) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        review.setUseful(0); // Изначально выставляем нулевой рейтинг
        Review createdReview = reviewsStorage.createReview(review);
                Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(createdReview.getReviewId())
                .build();
        feedStorage.addEvent(event);
        return createdReview;
    }

    public Review updateReview(Review updatedReview) {
        findById(updatedReview.getReviewId());
        Review review = reviewsStorage.updateReview(updatedReview);

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getReviewId())
                .build();
        feedStorage.addEvent(event);
        return reviewsStorage.updateReview(updatedReview);
    }

    public void deleteReview(Long id) {
        Review review = findById(id);
        reviewsStorage.deleteReview(id);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(id)
                .build();
        feedStorage.addEvent(event);
    }

    public void likeReview(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            int currentRating = review.getUseful();
            review.setUseful(currentRating + 1);
            updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public void dislikeReview(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            int currentRating = review.getUseful();
            int newUseful = currentRating - (currentRating >= 0 ? 2 : 1);
            review.setUseful(newUseful);
            updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public void removeLikeOrDislike(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            review.setUseful(0);
            updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public List<Review> findByFilmId(Long filmId) {
        if (filmService.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        return reviewsStorage.findByFilmId(filmId);
    }

    public List<Review> findTopNByFilmId(Long filmId, Integer count) {
        if (filmService.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        return reviewsStorage.findTopNByFilmId(filmId, count);
    }
}
