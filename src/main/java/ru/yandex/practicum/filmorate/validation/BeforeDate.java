package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeforeDateValidation.class)
@Past
public @interface BeforeDate {
    String message() default "Дата релиза фильма не может быть раньше чем {value}}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "1895-12-28";
}