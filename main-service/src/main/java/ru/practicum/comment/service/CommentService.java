package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentDto commentDto);

    void deleteUserComment(Long userId, Long commentId);

    CommentDto updateUserComment(Long userId, Long commentId, CommentDto commentDto);

    List<Comment> getAllEventComments(Long eventId);

    List<Comment> getByUserComment(Long userId);

}
