package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterDateValidation.class)
@Past
public @interface AfterDate {
    String message() default "Дата рождения не может быть в будущем";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
