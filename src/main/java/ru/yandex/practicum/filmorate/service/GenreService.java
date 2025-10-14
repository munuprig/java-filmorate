package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        return genreStorage.findGenres();
    }

    public Genre findGenreById(Long id) {
        if (genreStorage.findGenreById(id) == null) {
            throw new GenreNotFoundException("Жанра с таким id = " + id + " нет");
        } else {
            return genreStorage.findGenreById(id);
        }
    }
}
