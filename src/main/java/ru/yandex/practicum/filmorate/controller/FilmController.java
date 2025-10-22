package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public List<Film> findAllFilms() {
        log.info("GET / films");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long id) {
        log.info("GET / {}", id);
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST / film / {}", film.getName());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT / film / {}", film.getName());
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(
            @PathVariable("id") @Positive Long id) {
        log.info("DELETE / {}.", id);
        filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("PUT / {} / like / {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("DELETE / {} / like / {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") @Positive Long count,
                                  @RequestParam(required = false) Long genreId,
                                  @RequestParam(required = false) Integer year) {
        log.info("GET / popular with count={}, genreId={}, year={}", count, genreId, year);
        return filmService.findPopular(count, genreId, year);
    }
}
