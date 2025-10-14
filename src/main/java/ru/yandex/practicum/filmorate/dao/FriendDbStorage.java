package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO friends (user_id, friends_id, status)values (?, ?, ?)", userId, friendId, true);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friends_id = ?", userId, friendId);
    }

    @Override
    public List<User> findFriends(Long id) {
        return jdbcTemplate.query("SELECT * FROM users " +
                "WHERE id IN (" +
                "SELECT friends_id FROM friends " +
                "WHERE user_id = ? AND status = true)", new DataClassRowMapper<>(User.class), id);
    }

    @Override
    public List<User> findCommonFriends(Long id, Long friendId) {
        return jdbcTemplate.query("SELECT * FROM users " +
                "WHERE id IN (" +
                "SELECT friends_id FROM friends " +
                "WHERE user_id = ? " +
                "AND status = true " +
                "AND friends_id IN ( " +
                "SELECT friends_id FROM friends " +
                "WHERE user_id = ? " +
                "AND status = true))", new DataClassRowMapper<>(User.class), id, friendId);

    }
}
