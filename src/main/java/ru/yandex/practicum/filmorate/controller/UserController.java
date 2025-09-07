package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("users")
public class UserController {
    private static int idUserGenerator = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(++idUserGenerator);
        users.put(user.getId(), user);
        log.info("Пользователь {} создан.", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            log.info("Пользователь не найден.");
            throw new ValidationException("Пользователь не найден.");
        }
        validate(user);
        users.put(id, user);
        log.info("Пользователь {} обновлен.", user.getLogin());
        return user;
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
