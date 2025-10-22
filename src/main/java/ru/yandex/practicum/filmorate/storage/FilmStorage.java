package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    List<Film> findPopular(Long count, Long genreId, Integer year);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(Long id);

    boolean checkLikeOnFilm(Long filmId, Long userId);

    void deleteFilm(Long id);

    List<Film> getRecommendedFilms(Long userId);

    List<Film> findFilmsByDirectorSortedByYear(Long directorId);

    List<Film> findFilmsByDirectorSortedByLikes(Long directorId);
}
