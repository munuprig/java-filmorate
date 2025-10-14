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

    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна содержать символ @.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы.")
    private String login;

    private String name;

    @AfterDate
    private LocalDate birthday;
    private List<Long> friends;
}
