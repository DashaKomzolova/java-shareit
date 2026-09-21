package ru.practicum.shareit.item.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseJsonTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    void shouldSerializeAllFieldsCorrectly() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(5L);
        response.setText("Great tool!");
        response.setAuthorName("Ivan");
        response.setCreated(LocalDateTime.of(2026, 3, 15, 12, 30, 0));

        var result = json.write(response);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great tool!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-03-15T12:30:00");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = "{\"id\":5,\"text\":\"Great tool!\",\"authorName\":\"Ivan\",\"created\":\"2026-03-15T12:30:00\"}";

        CommentResponse parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(5L);
        assertThat(parsed.getText()).isEqualTo("Great tool!");
        assertThat(parsed.getAuthorName()).isEqualTo("Ivan");
        assertThat(parsed.getCreated()).isEqualTo(LocalDateTime.of(2026, 3, 15, 12, 30, 0));
    }
}
