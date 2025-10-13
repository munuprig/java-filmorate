package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        return findGenres().stream().filter(g -> g.getId() == id).findFirst();
    }

    @Override
    public List<Genre> findGenresByFilm(int id) {
        String sql = "SELECT DISTINCT fg.genre_id, genres.name " +
                "FROM film_genres AS fg " +
                "INNER JOIN genres ON genres.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY fg.genre_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
