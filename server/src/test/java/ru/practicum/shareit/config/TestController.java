package ru.practicum.shareit.config;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/notfound")
    public void throwNotFound() {
        throw new NotFoundException("Ресурс не найден");
    }

    @GetMapping("/conflict")
    public void throwConflict() {
        throw new EmailAlreadyUsedException("Email уже используется");
    }

    @GetMapping("/item-dont-belong")
    public void throwItemDontBelong() {
        throw new ItemDontBelongToUserException("Вещь не принадлежит пользователю");
    }

    @GetMapping("/item-unavailable")
    public void throwBadRequest() {
        throw new ItemUnavailableException("Вещь недоступна для бронирования");
    }

    @GetMapping("/booking-processing")
    public void throwBookingProcessing() {
        throw new BookingProcessingException("Ошибка при обработке бронирования");
    }

    @GetMapping("/comment-processing")
    public void throwCommentProcessing() {
        throw new CommentProcessingException("Ошибка при обработке комментария");
    }

    @GetMapping("/missing-header")
    public void throwMissingHeader(@RequestHeader("X-Sharer-User-Id") Long userId) {
    }

    @GetMapping("/method-test")
    public void methodTest() {
    }

    @GetMapping("/type-mismatch")
    public void typeMismatch(@RequestParam Long id) {
    }

    @GetMapping("/uncaught")
    public void throwUncaughtException() {
        throw new RuntimeException("Непредвиденная ошибка");
    }
}
