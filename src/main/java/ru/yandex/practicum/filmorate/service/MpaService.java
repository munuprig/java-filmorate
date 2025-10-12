package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(int id) {
        return mpaStorage.findMpaById(id).orElseThrow(() -> new MpaNotFoundException("Рейтинг MPA не найден."));
    }
}
