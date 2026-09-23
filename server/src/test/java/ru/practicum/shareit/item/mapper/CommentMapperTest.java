package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toComment_shouldMapTextFromRequest() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText("Отличная вещь!");

        Comment comment = CommentMapper.toComment(request);

        assertThat(comment.getText()).isEqualTo("Отличная вещь!");
    }

    @Test
    void toCommentResponse_shouldMapAllFieldsIncludingAuthorName() {
        User author = new User();
        author.setName("Иван");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отличная вещь!");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2026, 1, 1, 12, 0));

        CommentResponse response = CommentMapper.toCommentResponse(comment);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getText()).isEqualTo("Отличная вещь!");
        assertThat(response.getAuthorName()).isEqualTo("Иван");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
    }

    @Test
    void toCommentResponseList_shouldMapEachCommentInList() {
        User author = new User();
        author.setName("Иван");

        Comment first = new Comment();
        first.setId(1L);
        first.setAuthor(author);
        Comment second = new Comment();
        second.setId(2L);
        second.setAuthor(author);

        List<CommentResponse> result = CommentMapper.toCommentResponseList(List.of(first, second));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}
