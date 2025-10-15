package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {
    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    User findUserById(Long id);
}
