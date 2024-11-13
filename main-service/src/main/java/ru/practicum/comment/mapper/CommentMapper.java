package ru.practicum.comment.mapper;

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
    public static CommentDto mapToCommentDto(final Comment comment) {
        log.debug("Mapping Comment {} to CommentDto.", comment);
        Objects.requireNonNull(comment);
        return new CommentDto()
                .setId(comment.getId())
                .setUserId(comment.getUserId().getId())
                .setEventId(comment.getEventId().getId())
                .setContent(comment.getContent());
    }

    public static Comment mapTo(final CommentDto comment, final User user, final Event event) {
        log.debug("Mapping commentDto {} to comment.", comment);
        Objects.requireNonNull(comment);
        return new Comment()
                .setId(comment.getId())
                .setUserId(user)
                .setEventId(event)
                .setContent(comment.getContent());
    }



}
