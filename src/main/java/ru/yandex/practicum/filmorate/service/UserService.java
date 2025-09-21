package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        findUserById(user.getId());
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
    }

    public void addFriend(int id, int friendId) {
        if (id < 0 || friendId < 0) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        findUserById(id).getFriends().add(friendId);
        findUserById(friendId).getFriends().add(id);
    }

    public List<User> findAllFriends(int id) {
        return Optional.ofNullable(findUserById(id))              // Получаем пользователя по ID
                .map(User::getFriends)                    // Извлекаем множество друзей
                .orElse(Collections.emptySet())           // Возвращаем пустое множество, если друзей нет
                .stream()                                 // Создаем поток из множества друзей
                .map(this::findUserById)                  // По каждому другу находим объект User
                .collect(Collectors.toList());            // Собираем в итоговый список
    }

    public List<User> findCommonFriends(int id, int otherId) {
        List<User> commonFriends = findAllFriends(id);
        List<User> commonFriendsSecond = findAllFriends(otherId);
        commonFriends.retainAll(commonFriendsSecond);
        return commonFriends;
    }

    public void removeFriend(int id, int friendId) {
        findUserById(id).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(id);
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}