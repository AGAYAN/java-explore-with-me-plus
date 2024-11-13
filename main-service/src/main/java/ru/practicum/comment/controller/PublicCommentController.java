package ru.practicum.comment.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getByEvent(@PathVariable Long eventId) {
        List<Comment> comments = commentService.getAllEventComments(eventId);
        return ResponseEntity.ok(comments);
    }
}
