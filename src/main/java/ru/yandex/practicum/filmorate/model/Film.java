package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.validation.BeforeDate;

import java.time.LocalDate;
import java.util.List;

@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "Введите название фильма.")
    private String name;

    @NotNull
    @Size(max = 200, message = "Слишком длинное описание.")
    private String description;

    @BeforeDate
    @ReleaseDate(value = "1895-12-28", message = "Введите дату релиза не ранее 28 декабря 1895 года.")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть больше 0.")
    private Integer duration;

    @NonNull
    private Mpa mpa;

    private List<Long> likes;
    private List<Genre> genres;
    private String director;
}
