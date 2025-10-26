package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final DirectorService directorService;

    public List<Film> getRecommendedFilms(Long userId) {
        List<Film> films = filmStorage.getRecommendedFilms(userId);
        // Догружаем жанры и режиссеров для корректного ответа
        if (films != null && !films.isEmpty()) {
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));
            genreStorage.loadGenresForFilms(filmMap);
            directorService.loadDirectorsForFilms(filmMap);
        }
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        // проверка пользователей
        if (userStorage.findUserById(userId) == null || userStorage.findUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return filmStorage.getCommonFilms(userId, friendId);
    }
}