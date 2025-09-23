package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findFilmById(film.getId());
        return filmStorage.update(film);
    }

    public Film findFilmById(int id) {
        Film film = filmStorage.findFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Пользователь не найден");
        }
        return film;
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.findFilmById(id);
        throw new FilmNotFoundException("Пользователь не найден");
//        findFilmById(id).getLikes().add(userId);
    }

    public void removeLike(int id, int userId) {
        Film film = findFilmById(id);
        Set<Integer> likes = film.getLikes();
        if (!likes.contains(userId)) {
            throw new UserNotFoundException("Пользователь не нашелся среди тех, кто поставил лайк этому фильму.");
        }
        likes.remove(userId);
    }

    public List<Film> findPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
