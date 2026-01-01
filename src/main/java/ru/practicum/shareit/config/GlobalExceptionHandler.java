package ru.practicum.shareit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.EmailAlreadyUsedException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex) {
        log.warn(ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        String errorMessage = "Выявлены ошибки валидации.";

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Валидация не пройдена ({}): {}.", ex.getClass().getSimpleName(), errors);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .details(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex) {
        String errorMessage = "Произошла ошибка на сервере.";
        log.error("Необработанное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
