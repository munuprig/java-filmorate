package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int idUserGenerator = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(++idUserGenerator);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public void removeFriend(int id, int friendId) {

    }

    @Override
    public List<User> findFriends(int id) {
        return List.of();
    }

    @Override
    public List<User> findCommonFriends(int id, int otherId) {
        return List.of();
    }
}
