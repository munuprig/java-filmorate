package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IsEmptyException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreStorage genreStorage;

    public Film createFilm(Film film) {
        if (mpaService.findMpaById(film.getMpa().getId()) == null) {
            throw new ValidationException("Указанный MPA  не найден");
        } else if (film.getName().isEmpty()) {
            throw new IsEmptyException("Имя не указано");
        }
        if (film.getGenres() != null) {
            genreStorage.checkGenresExists(film.getGenres());
        }
        log.info("Фильм {} создан", film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.info("Id должен быть указан");
            throw new NullPointerException("Id должен быть указан");
        } else if (film.getName().isEmpty()) {
            throw new IsEmptyException("Имя не указано");
        }
        if (filmStorage.findFilmById(film.getId()) != null) {
            return filmStorage.updateFilm(film);
        }
        log.info("Не найден фильм");
        throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Film findFilmById(Long filmId) {
        if (filmStorage.findFilmById(filmId) != null) {
            return filmStorage.findFilmById(filmId);
        }
        throw new FilmNotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм отсуствует");
        } else if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь отсутсвует");
        }
        filmStorage.checkLikeOnFilm(filmId, userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм с таким ID не найден!");
        } else if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с таким ID не найден!");
        }
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> findPopular(Long count) {
        return filmStorage.findPopular(count);
    }
}