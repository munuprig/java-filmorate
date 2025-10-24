package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
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

    @NonNull
    private String content;

    @NonNull
    private boolean isPositive;

    @Positive(message = "Id пользователя не может быть меньше 0.")
    private Long userId;

    @Positive(message = "Id фильма не может быть меньше 0.")
    private Long filmId;

    private int useful;

}
