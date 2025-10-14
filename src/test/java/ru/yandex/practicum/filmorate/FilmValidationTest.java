package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FilmValidationTest {
    private Film film;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = Film.builder()
                .name("film1")
                .description("descriptionFilm1")
                .releaseDate(LocalDate.of(2000,12,12))
                .duration(100)
                .mpa(new Mpa(1L,"G"))
                .build();
    }

    @Test
    void testCreateWithEmptyName() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCreateWithDescriptionOver200Symbols() {
        film.setDescription("Это очень длинное описание фильма, которое должно превышать 200 символов," +
                " в котором расписывается все тонкости сюжетной линии, " +
                "хронология повествования и главное под какие снэки стоит смотреть этот фильм");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testCreateWithNegativeDuration() {
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validateFilmReleaseDate() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}