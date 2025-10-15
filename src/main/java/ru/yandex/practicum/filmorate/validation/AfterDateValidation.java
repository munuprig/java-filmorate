package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AfterDateValidation implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate nowDate;

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        nowDate = LocalDate.now();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isAfter(nowDate);
    }
}
