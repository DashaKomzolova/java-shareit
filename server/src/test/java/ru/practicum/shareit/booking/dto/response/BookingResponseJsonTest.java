package ru.practicum.shareit.booking.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseJsonTest {

    @Autowired
    private JacksonTester<BookingResponse> json;

    @Test
    void shouldSerializeAllFieldsCorrectly() throws Exception {
        BookingItemDto itemDto = new BookingItemDto(1L, "Drill");
        BookingBookerDto bookerDto = new BookingBookerDto(2L);

        BookingResponse response = new BookingResponse();
        response.setId(10L);
        response.setStart(LocalDateTime.of(2026, 1, 1, 10, 0, 0));
        response.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0, 0));
        response.setItem(itemDto);
        response.setBooker(bookerDto);
        response.setStatus(Status.APPROVED);

        var result = json.write(response);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-02T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = "{\"id\":10,\"start\":\"2026-01-01T10:00:00\",\"end\":\"2026-01-02T10:00:00\","
                + "\"item\":{\"id\":1,\"name\":\"Drill\"},\"booker\":{\"id\":2},\"status\":\"APPROVED\"}";

        BookingResponse parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(10L);
        assertThat(parsed.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0, 0));
        assertThat(parsed.getItem().getName()).isEqualTo("Drill");
        assertThat(parsed.getBooker().getId()).isEqualTo(2L);
        assertThat(parsed.getStatus()).isEqualTo(Status.APPROVED);
    }
}
