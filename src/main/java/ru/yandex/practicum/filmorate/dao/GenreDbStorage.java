package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

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
            return jdbcTemplate.query("SELECT * FROM genres " +
                            "WHERE id IN (SELECT genre_id FROM films_genre WHERE film_id = ?);",
                    new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public boolean checkGenresExists(List<Genre> genres) {
        for (Genre genre : genres) {
            if ((jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class),
                    genre.getId())).isEmpty()) {
                throw new GenreNotFoundException("Жанр с id = " + genre.getId() + " отсутствует");
            }
        }
        return true;
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT g.* FROM genres g " +
                "JOIN films_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id";
        return jdbcTemplate.query(sql, genreRowMapper, filmId);
    }

    @Override
    public void loadGenresForFilms(Map<Long, Film> filmMap) {
        String sql = "SELECT fg.film_id, g.* FROM genres g " +
                "JOIN films_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN (" + String.join(",", Collections.nCopies(filmMap.size(), "?")) + ") " +
                "ORDER BY fg.film_id, g.id";

        List<Object> filmIds = new ArrayList<>(filmMap.keySet());

        jdbcTemplate.query(sql, filmIds.toArray(), rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                Genre genre = Genre.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .build();
                film.getGenres().add(genre);
            }
        });
    }

}
