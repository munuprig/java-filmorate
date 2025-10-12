package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.findFilmById(film.getId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
        return filmStorage.update(film);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));
    }

    public void addLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        likeStorage.addLike(id, userId);
    }

    public void removeLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        likeStorage.removeLike(id, userId);
    }

    public List<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }
}