package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", new DataClassRowMapper<>(Genre.class));
    }


    @Override
    public Genre findGenreById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр с id = " + id + " не найден");
        }

    }

    @Override
    public List<Genre> findGenresByFilm(Long id) {
        try {
            return jdbcTemplate.query("SELECT * FROM genres WHERE id IN (SELECT genre_id FROM films_genre WHERE film_id = ?);", new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public boolean checkGenresExists(List<Genre> genres) {
        for (Genre genre : genres) {
            if ((jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class), genre.getId())).isEmpty()) {
                throw new ValidationException("Жанр с id = " + genre.getId() + " отсутствует");
            }
        }
        return true;
    }

}
