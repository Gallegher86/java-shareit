package ru.practicum.shareit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleNotFoundExceptionShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.error").value("Ресурс не найден"))
                .andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void handleEmailAlreadyUsedExceptionShouldReturn409() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict()) // 409
                .andExpect(jsonPath("$.error").value("Email уже используется"))
                .andExpect(jsonPath("$.errorCode").value(409));
    }

    @Test
    void handleItemDontBelongToUserExceptionShouldReturn409() throws Exception {
        mockMvc.perform(get("/test/item-dont-belong"))
                .andExpect(status().isConflict()) // 409
                .andExpect(jsonPath("$.error").value("Вещь не принадлежит пользователю"))
                .andExpect(jsonPath("$.errorCode").value(409));
    }

    @Test
    void handleItemUnavailableExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/item-unavailable"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error").value("Вещь недоступна для бронирования"))
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void handleBookingProcessingExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/booking-processing"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error").value("Ошибка при обработке бронирования"))
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void handleCommentProcessingExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/comment-processing"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error").value("Ошибка при обработке комментария"))
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void handleMissingRequestHeaderExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/missing-header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("В запросе отсутствует требуемый заголовок X-Sharer-User-Id."))
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void handleNoResourceFoundExceptionShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/non-existing-url"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ресурс по указанному пути не найден."))
                .andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void handleHttpRequestMethodNotSupportedExceptionShouldReturn405() throws Exception {
        mockMvc.perform(post("/test/method-test"))
                .andExpect(status().isMethodNotAllowed()) // 405
                .andExpect(jsonPath("$.error").value("Метод POST не поддерживается."))
                .andExpect(jsonPath("$.errorCode").value(405));
    }

    @Test
    void handleMethodArgumentTypeMismatchExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/type-mismatch")
                        .param("id", "notANumber"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error")
                        .value("Неверный формат параметра запроса 'id': 'notANumber'. Ожидается число."))
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void handleUncaughtExceptionShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/uncaught"))
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.error").value("Произошла ошибка на сервере."))
                .andExpect(jsonPath("$.errorCode").value(500));
    }
}