package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> findAllMpa();

    Mpa findMpaById(Long id);

    Mpa getMpaOfFilm(Long id);
}
