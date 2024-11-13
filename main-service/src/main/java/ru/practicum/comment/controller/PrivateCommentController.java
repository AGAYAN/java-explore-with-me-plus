package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
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
    public CommentDto addComments(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @RequestBody CommentDto commentDto) {

        commentDto.setUserId(userId);
        commentDto.setEventId(eventId);

        return commentService.addComment(commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentService.deleteUserComment(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto) {
        return commentService.updateUserComment(userId, commentId, commentDto);
    }

    @GetMapping
    public List<Comment> getByUserComment(@PathVariable Long userId) {
        return commentService.getByUserComment(userId);
    }
}
