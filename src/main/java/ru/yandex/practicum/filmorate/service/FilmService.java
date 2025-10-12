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

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findFilmById(film.getId());
        return filmStorage.update(film);
    }

    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));
    }

    public void addLike(int id, int userId) {
        likeStorage.addLike(id, userId);
    }

    public void removeLike(int id, int userId) {
        if (userId < 0 || id < 0) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        likeStorage.removeLike(id, userId);
    }

    public List<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }

}