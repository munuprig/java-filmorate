package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.AfterDate;

import java.time.LocalDate;
import java.util.List;

@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @Email
    @NotBlank(message = "Почта не может быть пустая")
    private String email;
    @NotBlank(message = "Login не должен быть пустым")
    private String login;
    private String name;
    @AfterDate
    private LocalDate birthday;
    private List<Long> friends;
}
