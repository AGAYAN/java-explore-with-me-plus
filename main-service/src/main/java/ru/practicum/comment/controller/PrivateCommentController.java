package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComments(
            @PathVariable @NonNull Long userId,
            @RequestParam @NonNull Long eventId,
            @RequestBody @Valid @NonNull CommentDto commentDto) {

        commentDto.setUserId(userId);
        commentDto.setEventId(eventId);

        return commentService.addComment(commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @NonNull Long commentId,
            @PathVariable @NonNull Long userId) {
        commentService.deleteUserComment(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable @NonNull Long userId,
            @PathVariable @NonNull Long commentId,
            @RequestBody @Valid @NonNull CommentDto commentDto) {
        CommentDto comment  = commentService.updateUserComment(userId, commentId, commentDto);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getByUserComment(@PathVariable @NonNull Long userId) {
        List<Comment> comments = commentService.getByUserComment(userId);
        return ResponseEntity.ok(comments);
    }
}
