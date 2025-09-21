package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

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
    public List<Film> findPopular(@RequestParam(defaultValue = "10", required = false) int count) {
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
    public void addFilmLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("PUT / {} / like / {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("DELETE / {} / like / {}", id, userId);
        filmService.removeLike(id, userId);
    }
}
