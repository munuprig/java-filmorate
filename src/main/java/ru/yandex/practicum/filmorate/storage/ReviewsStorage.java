package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsStorage {
    List<Review> findAll();                   // Список всех отзывов

    Review createReview(Review review);       // Создание отзыва

    Review updateReview(Review review);       // Обновление отзыва

    Review findReviewById(Long id);           // Поиск отзыва по ID

    void deleteReview(Long id);               // Удаление отзыва

    List<Review> findByFilmId(Long filmId);   // Выборка отзывов по фильму

    Review findByUserId(Long id, Long userId);

    List<Review> findTopNByFilmId(Long filmId, Integer limit); // Топ-N отзывов по полезности
}
