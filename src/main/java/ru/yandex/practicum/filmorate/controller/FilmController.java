package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("films")
public class FilmController {
    private int idFilmGenerator = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(++idFilmGenerator);
        films.put(film.getId(), film);
        log.info("Фильм под названием {} создан.", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            log.info("Фильм не найден.");
            throw new ValidationException("Фильм не найден.");
        }
        films.put(film.getId(), film);
        log.info("Фильм под названием {} обновлен.", film.getName());
        return film;
    }
}
