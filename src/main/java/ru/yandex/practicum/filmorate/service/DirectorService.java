package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAllDirectors() {
        log.info("Получение списка всех режиссеров");
        return directorStorage.findAllDirectors();
    }

    public Director getDirectorById(Long directorId) {
        log.info("Получение режиссера по ID");
        return directorStorage.findDirectorById(directorId)
                .orElseThrow(() -> {
                    log.warn("Режиссер с ID {} не найден", directorId);
                    return new DirectorNotFoundException("Режиссер с id: " + directorId + " не найден");
                });
    }

    public Director createDirector(Director newDirector) {
        log.info("Создание режиссера");
        return directorStorage.createDirector(newDirector);
    }

    public Director updateDirector(Director newDirector) {
        getDirectorById(newDirector.getId());
        Director updatedDirector = directorStorage.updateDirector(newDirector);
        log.info("Режиссер с id {} обновлен", newDirector.getId());
        return updatedDirector;
    }

    public void deleteDirector(Long directorId) {
        log.info("Попытка удаления режиссера");
        getDirectorById(directorId);
        directorStorage.deleteDirector(directorId);
        log.info("Режиссер  с id {} успешно удален", directorId);
    }

    public Set<Director> findDirectorByFilmId(Long filmId) {
        log.info("Попытка получения режиссеров по id фильма");
        return directorStorage.findDirectorByFilmId(filmId);
    }

    public void loadDirectorsForFilms(Map<Long, Film> filmMap) {
        log.info("Загрузка режиссеров для {} фильмов", filmMap.size());

        if (filmMap.isEmpty()) {
            log.debug("Передан пустой список фильмов");
            return;
        }

        // Получаем ID всех фильмов
        Set<Long> filmIds = filmMap.keySet();

        // Загружаем режиссеров для всех фильмов одним запросом
        Map<Long, Set<Director>> directorsByFilmId = directorStorage.findDirectorsByFilmIds(filmIds);

        // Назначаем режиссеров каждому фильму
        filmMap.forEach((filmId, film) -> {
            Set<Director> directors = directorsByFilmId.getOrDefault(filmId, Collections.emptySet());
            film.setDirectors(directors);
        });

        log.info("Загружены режиссеры для {} фильмов", directorsByFilmId.size());
    }
}

