package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewsRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Primary
@Slf4j
@Repository
public class ReviewsDBStorage implements ReviewsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewsRowMapper mapper;

    @Override
    public List<Review> findAll() {
        return jdbcTemplate.query("SELECT * FROM reviews", BeanPropertyRowMapper.newInstance(Review.class));
    }

    @Override
    public Review createReview(Review review) {

        String sqlQuery =
                "INSERT INTO reviews(content, is_positive, user_id, film_id) " + "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"reviewId"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update("UPDATE reviews SET content=?, is_positive=?, useful=? WHERE reviewId=?",
                review.getContent(), review.getIsPositive(), review.getUseful(), review.getReviewId());
        return review;
    }

    @Override
    public Review findReviewById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE reviewId = ?", mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewsNotFoundException("Отзыв с id = " + id + " не найден");
        }
    }

    @Override
    public void deleteReview(Long id) {
        jdbcTemplate.update("DELETE FROM reviews WHERE reviewId = ?", id);
    }

    @Override
    public List<Review> findByFilmId(Long filmId) {
        return jdbcTemplate.query("SELECT * FROM reviews WHERE film_id = ?",
                mapper, filmId);
    }

    @Override
    public List<Review> findTopNByFilmId(Long filmId, Integer limit) {
        return jdbcTemplate.query("SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?",
                mapper, filmId, limit);
    }
}
