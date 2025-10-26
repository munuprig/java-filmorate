package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(Long directorId);

    Set<Director> findDirectorByFilmId(Long filmId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long id);

    Map<Long, Set<Director>> findDirectorsByFilmIds(Set<Long> filmIds);
}
