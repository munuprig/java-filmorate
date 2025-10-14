package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> findGenres();

    Genre findGenreById(Long id);

    List<Genre> findGenresByFilm(Long id);

    boolean checkGenresExists(List<Genre> genres);
}
