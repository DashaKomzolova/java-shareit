package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public class CommentMapper {

    public static Comment toComment(CommentCreateRequest commentCreateRequest) {
        Comment comment = new Comment();
        comment.setText(commentCreateRequest.getText());
        return comment;
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());
        commentResponse.setAuthorName(comment.getAuthor().getName());
        commentResponse.setCreated(comment.getCreated());
        return commentResponse;
    }

    public static List<CommentResponse> toCommentResponseList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentResponse)
                .toList();
    }
}
