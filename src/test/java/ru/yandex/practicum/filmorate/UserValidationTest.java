package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {
    private User user;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = User.builder()
                .email("email@mail.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1994,12,13))
                .build();
    }

    @Test
    void validateUserEmailEmpty() {
        user = User.builder()
                .id(0x1L)
                .email("")
                .login("ya")
                .name("Ivan")
                .birthday(LocalDate.of(1986, 11, 15))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0x1L, violations.size());
        assertEquals("Электронная почта не может быть пустой.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserEmailIncorrect() {
        user = User.builder()
                .id(0x1L)
                .email("123ya.ru")
                .login("ya")
                .name("Ivan")
                .birthday(LocalDate.of(1986, 11, 15))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0x1L, violations.size());
        assertEquals("Электронная почта должна содержать символ @.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginEmpty() {
        user = User.builder()
                .id(0x1L)
                .email("123@ya.ru")
                .name("Ivan")
                .birthday(LocalDate.of(1986, 11, 15))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не может быть пустым.",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginSpace() {
        user = User.builder()
                .id(0x1L)
                .email("123@ya.ru")
                .login("y a")
                .name("Ivan")
                .birthday(LocalDate.of(1986, 11, 15))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0x1L, violations.size());
        assertEquals("Логин не может содержать пробелы.",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateUserBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}