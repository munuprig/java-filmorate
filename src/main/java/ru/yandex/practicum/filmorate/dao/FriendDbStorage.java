package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        if (findFriendById(userId).isEmpty() || findFriendById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        String sql = "merge into friendship (USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (findFriendById(userId).isEmpty() || findFriendById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        String sql = "DELETE FROM friendship WHERE user_id in (?, ?)";
        jdbcTemplate.update(sql, userId, friendId, userId, friendId);
    }

    @Override
    public List<User> findFriends(int id) {
        if (findFriendById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }

        String sql = "select * " +
                "from USERS, friendship " +
                "where USERS.USER_ID = friendship.FRIEND_ID " +
                "AND friendship.USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id);
    }

    @Override
    public List<User> findCommonFriends(int id, int otherId) {
        String sql = "select * from USERS u, friendship f, friendship o" +
                "where u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id, otherId);
    }

    private User makeFriend(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public Optional<User> findFriendById(int id) {
        String sql = "select * from users where user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("user_id"))
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
                    .build();
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }
}
