package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;

    private String content;

    private boolean isPositive;

    private Long userId;

    private Long filmId;

    private int useful;

}
