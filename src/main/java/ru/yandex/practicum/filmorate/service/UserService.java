package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FilmStorage filmStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Не задано имя пользователя, будет использован логин {}", user.getLogin());
        }
        log.info("Пользователь создан с логином {}", user.getLogin());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.info("Id пользователя должен быть указан");
            throw new UserNotFoundException("Id пользователя должен быть указан");
        }
        if (userStorage.findUserById(user.getId()) != null) {
            log.info("Пользователь с id = {} обновлен", user.getId());
            return userStorage.updateUser(user);
        }
        throw new UserNotFoundException("Пользователь не найден с id = " + user.getId());
    }

    public User findUserById(Long userId) {
        if (userStorage.findUserById(userId) != null) {
            return userStorage.findUserById(userId);
        }
        throw new UserNotFoundException("Пользователь не найден с id = " + userId);

    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.findUserById(userId) == null || userStorage.findUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        friendStorage.addFriend(userId, friendId);
        log.info("Пользователь {} стал другом пользователя {}",
                userStorage.findUserById(userId), userStorage.findUserById(friendId));
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.findUserById(userId) == null || userStorage.findUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        friendStorage.removeFriend(userId, friendId);
        log.info("Пользователи {} {} больше не друзья ",
                userStorage.findUserById(userId), userStorage.findUserById(friendId));
    }

    public List<User> findFriends(Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        log.info("Вот список друзей пользователя {} ", userStorage.findUserById(userId));
        return friendStorage.findFriends(userId);
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        if (userStorage.findUserById(userId) == null || userStorage.findUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return friendStorage.findCommonFriends(userId, friendId);
    }

    public void deleteUser(Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userStorage.deleteUser(userId);
        log.info("пользователь с id =  {}  удалён", userId);
    }

    public List<Film> getRecommendations(Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return filmStorage.getRecommendedFilms(userId);
    }
}