package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getRecommendedFilms(Long userId) {
        // перенесите сюда логику из FilmService.getRecommendedFilms
        return filmStorage.getRecommendedFilms(userId);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        // проверка пользователей
        if (userStorage.findUserById(userId) == null || userStorage.findUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return filmStorage.getCommonFilms(userId, friendId);
    }
}