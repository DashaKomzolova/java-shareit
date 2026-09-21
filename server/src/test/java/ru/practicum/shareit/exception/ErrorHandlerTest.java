package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleNotFound(new NotFoundException("не найден"));
        assertThat(response.getError()).isEqualTo("не найден");
    }

    @Test
    void handleDuplicate_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleDuplicate(new DuplicateException("дубликат"));
        assertThat(response.getError()).isEqualTo("дубликат");
    }

    @Test
    void handleNotOwnerException_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleNotOwnerException(new NotOwnerException("не владелец"));
        assertThat(response.getError()).isEqualTo("не владелец");
    }

    @Test
    void handleItemIsNotAvailable_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleItemIsNotAvailable(new ItemIsNotAvailable("недоступна"));
        assertThat(response.getError()).isEqualTo("недоступна");
    }

    @Test
    void handleDatesException_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleDatesException(new DatesException("неверные даты"));
        assertThat(response.getError()).isEqualTo("неверные даты");
    }

    @Test
    void handleItemIsABookerItemException_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response =
                errorHandler.handleItemIsABookerItemException(new ItemIsABookerItemException("это ваша вещь"));
        assertThat(response.getError()).isEqualTo("это ваша вещь");
    }

    @Test
    void handleBookingAlreadyProcessed_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response =
                errorHandler.handleBookingAlreadyProcessed(new BookingAlreadyProcessedException("уже обработано"));
        assertThat(response.getError()).isEqualTo("уже обработано");
    }

    @Test
    void handleUnknownState_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response = errorHandler.handleUnknownState(new UnknownStateException("неизвестный статус"));
        assertThat(response.getError()).isEqualTo("неизвестный статус");
    }

    @Test
    void handleCommentNotAllowed_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response =
                errorHandler.handleCommentNotAllowed(new CommentNotAllowedException("комментарий запрещен"));
        assertThat(response.getError()).isEqualTo("комментарий запрещен");
    }

    @Test
    void handleBookingDatesOverlap_shouldReturnErrorResponseWithMessage() {
        ErrorResponse response =
                errorHandler.handleBookingDatesOverlap(new BookingDatesOverlapException("пересечение дат"));
        assertThat(response.getError()).isEqualTo("пересечение дат");
    }
}
