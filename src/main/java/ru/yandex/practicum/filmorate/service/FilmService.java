package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
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
    private final FeedStorage feedStorage;

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
        // Проверка ID
        if (film.getId() == null) {
            log.info("Id должен быть указан");
            throw new NullPointerException("Id должен быть указан");
        }

        // Проверка существования фильма
        if (filmStorage.findFilmById(film.getId()) == null) {
            log.info("Не найден фильм");
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        // Проверяем MPA
        if (mpaService.findMpaById(film.getMpa().getId()) == null) {
            throw new ValidationException("Указанный MPA не найден");
        }

        // Проверяем жанры
        if (film.getGenres() != null) {
            genreStorage.checkGenresExists(film.getGenres());
        }

        // Обновляем фильм
        Film updatedFilm = filmStorage.updateFilm(film);

        // Возвращаем полный фильм с загруженными жанрами и режиссерами
        return findFilmById(updatedFilm.getId());
    }

    public Film findFilmById(Long filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film != null) {
            // Загружаем режиссеров
            Set<Director> directors = directorService.findDirectorByFilmId(filmId);
            film.setDirectors(directors);

            // Загружаем жанры
            List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
            film.setGenres(genres);

            return film;
        }
        throw new FilmNotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        loadDirectorsForFilms(films);
        loadGenresForFilms(films);
        return films;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм отсутствует");
        } else if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь отсутствует");
        }

        // Проверяем, не поставил ли уже пользователь лайк
        boolean canInsertLike = filmStorage.checkLikeOnFilm(filmId, userId);
        if (canInsertLike) {
            likeStorage.addLike(filmId, userId);
        }

        // В любом случае фиксируем событие в ленте (тесты ожидают событие даже при повторном запросе)
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build();
        feedStorage.addEvent(event);

        log.info("Пользователь {} {} лайк фильму {}", userId,
                canInsertLike ? "поставил" : "повторно запросил",
                filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм с таким ID не найден!");
        } else if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с таким ID не найден!");
        }
        likeStorage.removeLike(filmId, userId);

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build();
        feedStorage.addEvent(event);

        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public List<Film> findPopular(Long count, Long genreId, Integer year) {
        if (genreId != null) {
            if (genreStorage.findGenreById(genreId) == null) {
                throw new GenreNotFoundException("Жанр с id = " + genreId + " не найден");
            }
        }
        List<Film> films = filmStorage.findPopular(count, genreId, year);
        loadDirectorsForFilms(films);
        loadGenresForFilms(films);
        return films;
    }

    public void deleteFilm(Long filmId) {
        ensureFilmExists(filmId);
        filmStorage.deleteFilm(filmId);
        log.info("Фильм с id = {} удален", filmId);
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
        loadGenresForFilms(films);
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
        loadGenresForFilms(films);
        return films;
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if (sortBy == null) {
            throw new ValidationException("Параметр 'sortBy' обязателен и может быть 'likes' или 'year'");
        }

        String normalized = sortBy.toLowerCase();
        switch (normalized) {
            case "likes":
                return getFilmsByDirectorSortedByLikes(directorId);
            case "year":
                return getFilmsByDirectorSortedByYear(directorId);
            default:
                throw new ValidationException("Некорректное значение sortBy: '" + sortBy + "'. Разрешено 'likes' или 'year'");
        }
    }

    /**
     * Поиск фильмов по названию и/или режиссёру
     */
    public List<Film> searchFilms(String query, String by) {
        log.info("Поиск фильмов по запросу: '{}', по полям: '{}'", query, by);

        // Валидация параметров
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Запрос для поиска не может быть пустым");
        }

        if (by == null || by.trim().isEmpty()) {
            by = "title"; // По умолчанию поиск по названию
        }

        // Проверяем корректность параметра by
        String[] searchFields = by.split(",");
        for (String field : searchFields) {
            String trimmedField = field.trim().toLowerCase();
            if (!trimmedField.equals("title") && !trimmedField.equals("director")) {
                throw new ValidationException("Параметр 'by' может содержать только 'title' и/или 'director'");
            }
        }

        List<Film> films = filmStorage.searchFilms(query.trim(), by);
        loadDirectorsForFilms(films);
        loadGenresForFilms(films);
        return films;
    }

    /**
     * Рекомендации
     */
    public List<Film> getRecommendedFilms(Long userId) {
        List<Film> films = filmStorage.getRecommendedFilms(userId);
        loadGenresForFilms(films);
        loadDirectorsForFilms(films);
        return films;
    }

    private void loadDirectorsForFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));
            directorService.loadDirectorsForFilms(filmMap);
        }
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));

            // Загружаем жанры для всех фильмов пачкой
            genreStorage.loadGenresForFilms(filmMap);
        }
    }

    private void ensureFilmExists(Long filmId) {
        if (filmStorage.findFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        // Проверка пользователей через storage
        if (userStorage.findUserById(userId) != null && userStorage.findUserById(friendId) != null) {
            // оба пользователя существуют
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }

        List<Film> filmList = filmStorage.getCommonFilms(userId, friendId);
        log.info("Отгрузил {} общих фильмов для пользователей {} и {}", filmList.size(),
                userId, friendId);
        return filmList;
    }
}