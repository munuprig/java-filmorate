package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.ColumnMapRowMapper;
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
    public List<Film> findPopular(Long count, Long genreId, Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT f.id, ");
        sql.append("f.name, ");
        sql.append("f.description, ");
        sql.append("f.release_date, ");
        sql.append("f.duration, ");
        sql.append("l.USER_ID AS like_id, ");
        sql.append("mr.id AS mpa_id, ");
        sql.append("mr.name AS mpa_name, ");
        sql.append("g.id AS genre_id, ");
        sql.append("g.name AS genre_name, ");
        sql.append("COALESCE(like_count.cnt_like, 0) AS like_count ");
        sql.append("FROM films AS f ");
        sql.append("LEFT JOIN LIKES AS l ON (f.ID = l.FILM_ID) ");
        sql.append("LEFT JOIN RATING_MPA AS mr ON (f.RATING_MPA_ID = mr.ID) ");
        sql.append("LEFT JOIN FILMS_GENRE AS fg ON (f.ID = fg.film_id) ");
        sql.append("LEFT JOIN GENRES AS g ON (fg.genre_id = g.ID) ");
        sql.append("LEFT JOIN (SELECT FILM_ID, COUNT(user_id) AS cnt_like FROM likes GROUP BY FILM_ID) like_count ON (f.id = like_count.FILM_ID) ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (genreId != null) {
            sql.append("AND f.id IN (SELECT film_id FROM films_genre WHERE genre_id = ?) ");
            params.add(genreId);
        }
        
        if (year != null) {
            sql.append("AND EXTRACT(YEAR FROM f.release_date) = ? ");
            params.add(year);
        }
        
        sql.append("ORDER BY COALESCE(like_count.cnt_like, 0) DESC, f.id ");
        
        List<Film> films = jdbcTemplate.query(sql.toString(), mapper, params.toArray());
        
        // Убираем дубликаты и возвращаем уникальные фильмы
        Map<Long, Film> uniqueFilms = new LinkedHashMap<>();
        for (Film film : films) {
            uniqueFilms.put(film.getId(), film);
        }
        
        // Применяем LIMIT после дедупликации
        List<Film> result = new ArrayList<>(uniqueFilms.values());
        if (result.size() > count) {
            result = result.subList(0, count.intValue());
        }
        return result;
    }

    @Override
    public boolean checkLikeOnFilm(Long filmId, Long userId) {
        List<Map<String, Object>> result = jdbcTemplate.query("SELECT user_id FROM likes " +
                        "WHERE film_id = ? AND user_id = ?", new ColumnMapRowMapper(), filmId, userId);
        if (!result.isEmpty()) {
            throw new ValidationException("Пользователь с id = " + userId + " уже поставил лайк");
        }
        return true;
    }

}
