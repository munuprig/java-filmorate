package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreStorage genreStorage;
    private final DirectorService directorService;

    public Film createFilm(Film film) {
        if (mpaService.findMpaById(film.getMpa().getId()) == null) {
            throw new ValidationException("Указанный MPA  не найден");
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
        }
        if (filmStorage.findFilmById(film.getId()) != null) {
            return filmStorage.updateFilm(film);
        }
        log.info("Не найден фильм");
        throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Film findFilmById(Long filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film != null) {
            Set<Director> directors = directorService.findDirectorByFilmId(filmId);
            film.setDirectors(directors);
            return film;
        }
        throw new FilmNotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        loadDirectorsForFilms(films);
        return films;
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

    public List<Film> findPopular(Long count, Long genreId, Integer year) {
        return filmStorage.findPopular(count, genreId, year);
    }

    public void deleteFilm(Long filmId) {
        findFilmById(filmId);
        filmStorage.deleteFilm(filmId);
        log.info("Фильм с id = {} удален", filmId);
    }

    private void loadDirectorsForFilm(Film film) {
        if (film != null) {
            Set<Director> directors = directorService.findDirectorByFilmId(film.getId());
            film.setDirectors(directors);
        }
    }

    private void loadDirectorsForFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));
            directorService.loadDirectorsForFilms(filmMap);
        }
    }

    /**
     * Получить фильмы режиссера отсортированные по лайкам
     */
    public List<Film> getFilmsByDirectorSortedByLikes(Long directorId) {
        log.info("Получение фильмов режиссера {} отсортированных по лайкам", directorId);
        // Проверяем что режиссер существует
        directorService.getDirectorById(directorId);
        List<Film> films = filmStorage.findFilmsByDirectorSortedByLikes(directorId);
        loadDirectorsForFilms(films);
        return films;
    }

    /**
     * Получить фильмы режиссера отсортированные по годам
     */
    public List<Film> getFilmsByDirectorSortedByYear(Long directorId) {
        log.info("Получение фильмов режиссера {} отсортированных по годам", directorId);
        // Проверяем что режиссер существует
        directorService.getDirectorById(directorId);
        List<Film> films = filmStorage.findFilmsByDirectorSortedByYear(directorId);
        loadDirectorsForFilms(films);
        return films;
    }
}