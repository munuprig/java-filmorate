package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreStorage {
    List<Genre> findGenres();

    Genre findGenreById(Long id);

    List<Genre> findGenresByFilm(Long id);

    boolean checkGenresExists(List<Genre> genres);

    List<Genre> findGenresByFilmId(Long filmId);

    void loadGenresForFilms(Map<Long, Film> filmMap);
}
