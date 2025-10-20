package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmStorage.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
class FilmDbStorageTest {
    @Autowired
    private FilmStorage storage;

    @Test
    void createFilm() {
        storage.createFilm(new Film(
                1L,
                "updateName",
                "description",
                LocalDate.of(1991, 1, 12),
                200,
                new Mpa(1L, "G"),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashSet<>()
        ));
        Film film = storage.findFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "updateName");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 200);
    }

    @Test
    @Sql(scripts = {"/test-get-films.sql"})
    void updateFilm() {
        storage.updateFilm(new Film(
                1L,
                "updateName",
                "description",
                LocalDate.of(1991, 01, 12),
                200,
                new Mpa(1L, "G"),
                null,
                null,
                new HashSet<>()
        ));

        Film film = storage.findFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "updateName");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 200);

    }

    @Test
    @Sql(scripts = {"/test-get-films.sql"})
    void getFilm() {
        Film film = storage.findFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "film_name1");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 60);
    }

    @Test
    @Sql(scripts = {"/test-get-films.sql"})
    void getAllFilms() {
        List<Film> films = storage.findAllFilms();
        System.out.println(films);

        assertThat(films.get(0)).hasFieldOrPropertyWithValue("name", "film_name1");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("description", "description");
        assertThat(films.get(0)).hasFieldOrProperty("releaseDate");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("duration", 60);

        assertThat(films.get(1)).hasFieldOrPropertyWithValue("name", "film_name2");
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("description", "description");
        assertThat(films.get(1)).hasFieldOrProperty("releaseDate");
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("duration", 40);

        assertThat(films.get(2)).hasFieldOrPropertyWithValue("name", "film_name3");
        assertThat(films.get(2)).hasFieldOrPropertyWithValue("description", "description");
        assertThat(films.get(2)).hasFieldOrProperty("releaseDate");
        assertThat(films.get(2)).hasFieldOrPropertyWithValue("duration", 74);
    }
}