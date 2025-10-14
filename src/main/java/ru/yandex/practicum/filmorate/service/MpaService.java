package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(Long id) {

        if (mpaStorage.findMpaById(id) == null) {
            throw new MpaNotFoundException("Рейтинга с таким id = " + id + " нет");
        } else {
            return mpaStorage.findMpaById(id);
        }
    }
}
