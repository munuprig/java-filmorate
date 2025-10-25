package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;


import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, DirectorRowMapper directorRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorRowMapper = directorRowMapper;
    }

    @Override
    public List<Director> findAllDirectors() {
        String sql = "SELECT id, name FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> findDirectorById(Long directorId) {
        String sql = "SELECT id, name FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, directorId);

        if (directors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(directors.getFirst());
    }

    @Override
    public Director createDirector(Director newDirector) {
        String sql = "INSERT INTO directors (name) " +
                "VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newDirector.getName());
            return ps;
        }, keyHolder);

        Long directorId = getGeneratedId(keyHolder);
        newDirector.setId(directorId);

        return newDirector;
    }

    private Long getGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DataAccessException("Не удалось получить сгенерированный ID") {
            };
        }
        return key.longValue();
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());

        if (updatedRows == 0) {
            throw new DirectorNotFoundException("Режиссер с id=" + director.getId() + " не найден");
        }

        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        // 1. Сначала удаляем связи с фильмами
        String deleteLinksSql = "DELETE FROM films_director WHERE director_id = ?";
        jdbcTemplate.update(deleteLinksSql, directorId);

        // 2. Затем удаляем самого режиссера
        String deleteDirectorSql = "DELETE FROM directors WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(deleteDirectorSql, directorId);

        if (rowsDeleted == 0) {
            throw new DirectorNotFoundException("Режиссер с id = " + directorId + " не найден");
        }
    }

    @Override
    public Set<Director> findDirectorByFilmId(Long filmId) {
        String sql = "SELECT d.id, d.name FROM directors AS d " +
                "JOIN films_director AS fd ON d.id = fd.director_id WHERE fd.film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, directorRowMapper, filmId));
    }

    @Override
    public Map<Long, Set<Director>> findDirectorsByFilmIds(Set<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }

        String sql = "SELECT fd.film_id, d.id, d.name FROM films_director fd " +
                "JOIN directors d ON fd.director_id = d.id " +
                "WHERE fd.film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") " +
                "ORDER BY fd.film_id, d.id";

        Map<Long, Set<Director>> directorsMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getLong("id");
            String directorName = rs.getString("name");
            directorsMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>())
                    .add(new Director(directorId, directorName));
        });

        return directorsMap;
    }
}
