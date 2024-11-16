package ru.practicum.comment.mapper;

import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.Objects;

@Slf4j
@UtilityClass
public class CommentMapper {

  public CommentDto mapToCommentDto(final Comment comment) {
    log.debug("Mapping Comment {} to CommentDto.", comment);
    Objects.requireNonNull(comment);
    return new CommentDto()
        .setId(comment.getId())
        .setUserId(comment.getUser().getId())
        .setEventId(comment.getEvent().getId())
        .setContent(comment.getContent())
        .setCreated(comment.getCreated())
        .setInitiator(comment.isInitiator());
  }

  public List<CommentDto> mapToCommentDto(final List<Comment> comments) {
    if (comments == null || comments.isEmpty()) {
      return List.of();
    }
    return comments.stream()
        .map(CommentMapper::mapToCommentDto)
        .toList();
  }

  public Comment mapTo(final CommentDto comment, final User user, final Event event) {
    log.debug("Mapping commentDto {} to comment.", comment);
    Objects.requireNonNull(comment);
    return new Comment()
        .setId(comment.getId())
        .setUser(user)
        .setEvent(event)
        .setContent(comment.getContent());
  }

}
