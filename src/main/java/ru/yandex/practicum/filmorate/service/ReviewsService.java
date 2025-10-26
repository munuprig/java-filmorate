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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;
    private final FeedStorage feedStorage;

    public List<Review> findAllReviews() {
        return reviewsStorage.findAll();
    }

    public Review findById(Long id) {
        try {
            return reviewsStorage.findReviewById(id);
        } catch (ReviewsNotFoundException e) {
            throw new ReviewsNotFoundException("Отзыв не найден с id = " + id);
        }
    }

    public Review createReview(Review review) {
        // Сначала проверяем валидацию полей
        if (review.getUserId() == null || review.getUserId() <= 0) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (review.getFilmId() == null || review.getFilmId() <= 0) {
            throw new FilmNotFoundException("Фильм не найден");
        }

        // Затем проверяем существование пользователя и фильма
        if (userStorage.findUserById(review.getUserId()) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (filmService.findFilmById(review.getFilmId()) == null) {
            throw new FilmNotFoundException("Фильм не найден");
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
        Review existingReview = findById(updatedReview.getReviewId());

        // Сохраняем оригинальные user_id и film_id
        updatedReview.setUserId(existingReview.getUserId());
        updatedReview.setFilmId(existingReview.getFilmId());
        // Поле useful нельзя менять через обычный update
        updatedReview.setUseful(existingReview.getUseful());

        Review review = reviewsStorage.updateReview(updatedReview);

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getReviewId())
                .build();
        feedStorage.addEvent(event);
        return review;
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

    public Review likeReview(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            int currentRating = review.getUseful();
            review.setUseful(currentRating + 1);
            return reviewsStorage.updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public Review dislikeReview(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            int currentRating = review.getUseful();
            // Дизлайк уменьшает полезность на 1
            review.setUseful(currentRating - 1);
            return reviewsStorage.updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public Review removeLikeOrDislike(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            review.setUseful(0);
            return reviewsStorage.updateReview(review);
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

    public List<Review> getReviews(Long filmId, Integer count) {
        if (filmId != null && filmId > 0) {
            if (filmService.findFilmById(filmId) == null) {
                throw new FilmNotFoundException("Фильм не найден");
            }
            return reviewsStorage.findTopNByFilmId(filmId, count);
        }
        return reviewsStorage.findAll();
    }
}
