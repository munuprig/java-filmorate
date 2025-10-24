package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public List<Review> findAllReviews() {
        return reviewsStorage.findAll();
    }

    public Review findById(Long id) {
        if (reviewsStorage.findReviewById(id) != null) {
            return reviewsStorage.findReviewById(id);
        }
        throw new ReviewsNotFoundException("Отзыв не найден с id = " + id);
    }

    public Review createReview(Review review) {
        if (review.getIsPositive() == null || review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        review.setUseful(0); // Изначально выставляем нулевой рейтинг
        return reviewsStorage.createReview(review);
    }

    public Review updateReview(Review updatedReview) {
        if (findById(updatedReview.getReviewId()) != null) {
            throw new IllegalArgumentException("Отзыв не найден");
        }
        return reviewsStorage.updateReview(updatedReview);
    }

    public void deleteReview(Long id) {
        reviewsStorage.deleteReview(id);
    }

    public void likeReview(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Long filmId = findById(reviewId).getFilmId();
        if (filmId > 0 || filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм не найден");
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
        Long filmId = findById(reviewId).getFilmId();
        if (filmId > 0 || filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            int currentRating = review.getUseful();
            review.setUseful(currentRating - 1);
            updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public void removeLikeOrDislike(Long reviewId, Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (filmStorage.findFilmById(findById(reviewId).getFilmId()) == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        Review review = findById(reviewId);
        if (review != null) {
            review.setUseful(0); // Убираем оценку
            updateReview(review);
        } else {
            throw new ReviewsNotFoundException("Отзыв не найден");
        }
    }

    public List<Review> findByFilmId(Long filmId) {
        return reviewsStorage.findByFilmId(filmId);
    }

    public List<Review> findTopNByFilmId(Long filmId, Integer count) {
        return reviewsStorage.findTopNByFilmId(filmId, count);
    }
}
