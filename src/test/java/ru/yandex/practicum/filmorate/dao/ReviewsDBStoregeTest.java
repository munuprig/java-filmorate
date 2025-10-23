package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {ReviewsStorage.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
public class ReviewsDBStoregeTest {
    @Autowired
    private final ReviewsStorage storage;

    @Test
    void createReviews() {
        storage.createReview(new Review(
                1L,
                "aaa",
                false,
                1L,
                1L,
                0
        ));

        Review review = storage.findReviewById(1L);
        assertThat(review).hasFieldOrPropertyWithValue("content", "aaa");
    }
}
