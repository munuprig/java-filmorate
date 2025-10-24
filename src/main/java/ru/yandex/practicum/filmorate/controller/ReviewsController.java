package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewsService reviewService;

    /**
     * Создать новый отзыв
     */
    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("POST / film / {}", review.getReviewId());
        return reviewService.createReview(review);
    }

    /**
     * Получить список всех отзывов
     */
    @GetMapping
    public List<Review> getAllReviews() {
        log.info("GET / '/reviews'");
        return reviewService.findAllReviews();
    }

    /**
     * Найти отзыв по идентификатору
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("GET / '/reviews/{id}'");
        return reviewService.findById(id);
    }

    /**
     * Изменить существующий отзыв
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review updatedReview) {
        log.info("PUT / film / {}", updatedReview.getReviewId());
        return reviewService.updateReview(updatedReview);
    }

    /**
     * Удалить отзыв по идентификатору
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("DELETE / {}.", id);
        reviewService.deleteReview(id);
    }

    /**
     * Получить отзывы по идентификатору фильма
     */
    @GetMapping("/reviews?filmId={filmId}&count={count}")
    public List<Review> getReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        log.info("GET /reviews?filmId= {} &count= {}.", filmId, count);
        if (filmId != null && filmId > 0) {
            return reviewService.findTopNByFilmId(filmId, count);
        } else {
            return reviewService.findAllReviews();
        }
    }

    @GetMapping("/reviews?filmId={filmId}")
    public List<Review> getReviewsByFilmIdCountTen(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        log.info("GET /reviews?filmId= {} &count=10.", filmId);
        if (filmId != null && filmId > 0) {
            return reviewService.findTopNByFilmId(filmId, count);
        } else {
            return reviewService.findAllReviews();
        }
    }


    /**
     * Оценить отзыв положительной оценкой (лайком)
     */
    @PutMapping("/{id}/like/{userId}")
    public Review likeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /{}/like/{}.", id, userId);
        reviewService.likeReview(id, userId);
        return reviewService.findById(id);
    }

    /**
     * Оценить отзыв отрицательной оценкой (дизлайком)
     */
    @PutMapping("/{id}/dislike/{userId}")
    public Review dislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /{}/dislike/{}.", id, userId);
        reviewService.dislikeReview(id, userId);
        return reviewService.findById(id);
    }

    /**
     * Удалить свою оценку отзыва (либо лайк, либо дизлайк)
     */
    @DeleteMapping("/{id}/{action}/{userId}")
    public Review removeUserVote(@PathVariable Long id, @PathVariable String action, @PathVariable Long userId) {
        switch (action.toLowerCase()) {
            case "like", "dislike":
                reviewService.removeLikeOrDislike(id, userId);
                break;
            default:
                throw new IllegalArgumentException("Неверное действие: " + action);
        }
        return reviewService.findById(id);
    }
}
