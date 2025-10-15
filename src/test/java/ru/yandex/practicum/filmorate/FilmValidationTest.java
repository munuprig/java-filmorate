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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void validateFilmName() {
        film = Film.builder()
                .id(0x1L)
                .name("")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(100)
                .mpa(new Mpa(0x1L, "G"))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0x1L, violations.size());
        assertEquals("Введите название фильма.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDescription() {
        film = Film.builder()
                .id(0x1L)
                .name("Film")
                .description("Из-под покрова тьмы ночной,\n" +
                        "Из чёрной ямы страшных мук\n" +
                        "Благодарю я всех богов\n" +
                        "За мой непокорённый дух.\n" +
                        "\n" +
                        "И я, попав в тиски беды,\n" +
                        "Не дрогнул и не застонал,\n" +
                        "И под ударами судьбы\n" +
                        "Я ранен был, но не упал.\n" +
                        "\n" +
                        "Т")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(100)
                .mpa(new Mpa(0x1L, "G"))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0x1L, violations.size());
        assertEquals("Слишком длинное описание.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmReleaseDate() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validateFilmDuration() {
        film = Film.builder()
                .id(0x1L)
                .name("Film")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(-100)
                .mpa(new Mpa(0x1L, "G"))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0x1L, violations.size());
        assertEquals("Продолжительность фильма должна быть больше 0.",
                violations.iterator().next().getMessage());
    }
}