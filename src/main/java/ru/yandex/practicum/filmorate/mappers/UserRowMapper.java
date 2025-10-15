package ru.yandex.practicum.filmorate.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class UserRowMapper implements RowMapper<User> {

    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long userId = resultSet.getLong("id");

        User user = User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new ArrayList<>())
                .build();

        // если есть friends:
        if (resultSet.getLong("FRIENDS_id") != 0) {
            if (!user.getFriends().contains(resultSet.getLong("FRIENDS_id"))) {
                user.getFriends().add(resultSet.getLong("FRIENDS_id"));
            }
        }
        return user;
    }
}
