package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.info("GET / users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Long id) {
        log.info("GET / users / {}", id);
        return userService.findUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") Long id) {
        log.info("GET / {} / friends", id);
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        log.info("GET / {} / friends / common / {}", id, otherId);
        return userService.findCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST / user / {}", user.getLogin());
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT / user / {}", user.getLogin());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("PUT / {} / friends / {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("DELETE / {} / friends / {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") @Positive Long id) {
        log.info("DELETE / {} ", id);
        userService.deleteUser(id);
    }
}
