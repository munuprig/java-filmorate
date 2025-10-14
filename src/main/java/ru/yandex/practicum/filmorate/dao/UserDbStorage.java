package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@AllArgsConstructor
@Primary
@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT " +
                "u.ID, " +
                "u.EMAIL, " +
                "u.LOGIN, " +
                "u.NAME, " +
                "u.BIRTHDAY, " +
                "f.USER2_ID " +
                "FROM USERS u " +
                "LEFT JOIN FRIENDS f ON (f.USER1_ID  = u.ID)", mapper);
        Set<User> uniqueUser = new TreeSet<>(Comparator.comparing(User::getId));
        uniqueUser.addAll(users);
        return new ArrayList<>(uniqueUser);
    }

    @Override
    public User findUserById(Long id) {
        List<User> users = jdbcTemplate.query("SELECT " +
                "u.ID, " +
                "u.EMAIL, " +
                "u.LOGIN, " +
                "u.NAME, " +
                "u.BIRTHDAY, " +
                "f.USER2_ID " +
                "FROM USERS AS u " +
                "LEFT JOIN FRIENDS AS f ON (f.USER1_ID  = u.ID)" +
                "WHERE u.id = ?", mapper, id);
        if (users.size() == 0) {
            return null;
        }
        return users.get(0);
    }


    @Override
    public User createUser(User user) {
        String sqlQuery =
                "INSERT INTO users (email, login, name, birthday)values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery =
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }
}