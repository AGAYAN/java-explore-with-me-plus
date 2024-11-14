package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.GetCommentsAdminRequest;
import ru.practicum.comment.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

  private final CommentService commentService;

  @GetMapping
  public ResponseEntity<List<CommentDto>> getComments(
      @RequestParam("eventId") @Positive Long eventId,
      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
      @RequestParam(defaultValue = "10") @Positive Integer size) {
    log.info("Request received GET /admin/comments?eventId={}&from={}&size={}", eventId, from,
        size);
    final List<CommentDto> result =
        commentService.getAllEventComments(new GetCommentsAdminRequest(eventId, from, size));
    log.info("Sending event list size {}.", result.size());
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{commentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeComment(@PathVariable("commentId") Long commentId) {
    log.info("Request received Delete /admin/comments/{}", commentId);
    commentService.delete(commentId);
    log.info("Comment was deleted.");
  }

}
