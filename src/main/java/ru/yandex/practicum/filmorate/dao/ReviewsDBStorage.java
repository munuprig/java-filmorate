package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.ReviewsRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("INSERT INTO reviews(content, is_positive, user_id, film_id) " +
                    "VALUES (?, ?, ?, ?)", new String[]{"reviewId"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update("UPDATE reviews SET content=?, is_positive=? WHERE reviewId=?", review.getContent(),
                review.isPositive(), review.getReviewId());
        return review;
    }

    @Override
    public Review findReviewById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE reviewId = ?",
                mapper, id);
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
