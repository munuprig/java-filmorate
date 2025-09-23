package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("GET / films");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") int id) {
        log.info("GET / {}", id);
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10")@Positive int count) {
        log.info("GET / popular");
        return filmService.findPopular(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST / film / {}", film.getName());
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT / film / {}", film.getName());
        filmService.update(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addFilmLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (FilmNotFoundException | UserNotFoundException e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeFilmLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (FilmNotFoundException | UserNotFoundException e) {
            throw e;
        }
    }
}
