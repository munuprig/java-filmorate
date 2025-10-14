package ru.yandex.practicum.filmorate.exception;

public class IsEmptyException extends RuntimeException {
  public IsEmptyException(String message) {
    super(message);
  }
}
