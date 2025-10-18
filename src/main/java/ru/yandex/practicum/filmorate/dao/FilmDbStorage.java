package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@AllArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "l.USER_ID AS like_id, " +
                "mr.id AS mpa_id, " +
                "mr.name AS mpa_name, " +
                "g.id AS genre_id, " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT JOIN LIKES AS l ON (f.ID = l.FILM_ID) " +
                "LEFT JOIN RATING_MPA AS mr ON (f.RATING_MPA_ID  = mr.ID) " +
                "LEFT JOIN FILMS_GENRE AS fg ON (f.ID  = fg.film_id) " +
                "LEFT JOIN GENRES AS g ON (fg.genre_id = g.ID);", mapper);
        Set<Film> uniqueFilms = new TreeSet<>(Comparator.comparing(Film::getId));
        uniqueFilms.addAll(films);
        return new ArrayList<>(uniqueFilms);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_mpa_id)" +
                "values (?, ?, ?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        if (film.getGenres() != null) {
            Set<Genre> genres = new LinkedHashSet<>(film.getGenres());
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO films_genre (film_id, genre_id)values(?,?)", film.getId(),
                        genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery =
                "UPDATE films " +
                        "SET name = ?, description = ?, release_date = ?, duration = ?, rating_mpa_id = ? " +
                        "WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film findFilmById(Long filmId) {
        List<Film> films = jdbcTemplate.query("SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "l.USER_ID AS like_id, " +
                "mr.id AS mpa_id, " +
                "mr.name AS mpa_name, " +
                "g.id AS genre_id , " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT JOIN LIKES AS l ON (f.ID = l.FILM_ID) " +
                "LEFT JOIN RATING_MPA AS mr ON (f.RATING_MPA_ID  = mr.ID) " +
                "LEFT JOIN FILMS_GENRE AS fg ON (f.ID  = fg.film_id) " +
                "LEFT JOIN GENRES AS g ON (fg.genre_id = g.ID)" +
                "WHERE F.ID = ?;", mapper, filmId);
        if (films.size() == 0) {
            return null;
        }
        return films.get(0);
    }

    @Override
    public List<Film> findPopular(Long count) {
        return jdbcTemplate.query(
                "SELECT ID, NAME, cnt_like " +
                        "FROM PUBLIC.FILMS f " +
                        "LEFT JOIN (select FILM_ID, COUNT(user_id) cnt_like from likes group by FILM_ID) l ON " +
                        "(f.id = l.FILM_ID) " +
                        "ORDER BY l.cnt_like DESC " +
                        "LIMIT ?", new DataClassRowMapper<>(Film.class), count);
    }

    @Override
    public boolean checkLikeOnFilm(Long filmId, Long userId) {
        if ((jdbcTemplate.query("SELECT user_id FROM likes " +
                        "WHERE film_id = ? AND user_id = ?", new ColumnMapRowMapper(), filmId,
                userId)).contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " уже поставил лайк");
        }
        return true;
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getRecommendedFilms(Long userId) {
        String similarUserQuery = """
            SELECT l2.user_id as similar_user_id, 
                   COUNT(*) as common_likes
            FROM likes l1
            JOIN likes l2 ON l1.film_id = l2.film_id 
            WHERE l1.user_id = ? 
              AND l2.user_id != ?
            GROUP BY l2.user_id
            ORDER BY common_likes DESC
            LIMIT 1
            """;

        List<Map<String, Object>> similarUsers = jdbcTemplate.query(similarUserQuery,
                new ColumnMapRowMapper(), userId, userId);

        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Long similarUserId = (Long) similarUsers.get(0).get("similar_user_id");

        String recommendedFilmsQuery = """
            SELECT f.id, 
                   f.name, 
                   f.description, 
                   f.release_date, 
                   f.duration,
                   mr.id AS mpa_id,
                   mr.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   l.user_id AS like_id
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id AND l.user_id = ?
            LEFT JOIN rating_mpa mr ON f.rating_mpa_id = mr.id
            LEFT JOIN films_genre fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            WHERE f.id IN (
                SELECT film_id 
                FROM likes 
                WHERE user_id = ?
            )
            AND l.user_id IS NULL
            ORDER BY (
                SELECT COUNT(*) 
                FROM likes l2 
                WHERE l2.film_id = f.id
            ) DESC
            """;

        List<Film> recommendedFilms = jdbcTemplate.query(recommendedFilmsQuery, mapper, userId, similarUserId);

        Set<Film> uniqueFilms = new TreeSet<>(Comparator.comparing(Film::getId));
        uniqueFilms.addAll(recommendedFilms);

        return new ArrayList<>(uniqueFilms);
    }
}
