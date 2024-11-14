package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.GetCommentsAdminRequest;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final EventRepository eventRepository;

  /**
   * http://localhost:8080/users/{userId}/comments?eventId={eventId}
   * <p> Saves a new comment data initiated by a current user.
   */
  @Override
  public CommentDto addComment(final CommentDto commentDto) {
    User user = fetchUser(commentDto.getUserId());
    Event event = fetchEvent(commentDto.getEventId());

    if (!State.PUBLISHED.equals(event.getState())) {
      log.warn("Can not add comment to the event that is not published, event state = {}",
          event.getState());
      throw new ConflictException("Cannot save comments for not published event.");
    }

    Comment comment = CommentMapper.mapTo(commentDto, user, event);

    comment.setCreated(LocalDateTime.now());

    if (user.getId().equals(event.getInitiator().getId())) {
      comment.setInitiator(true);
    }

    Comment savedComment = commentRepository.save(comment);
    return CommentMapper.mapToCommentDto(savedComment);

  }

  /**
   * http://localhost:8080/users/{userId}/comments/{commentId}
   * <p> Deletes current user's comment
   */
  @Override
  public void delete(final Long userId, final Long commentId) {
    Comment comment = fetchComment(commentId);

    if (!comment.getUser().getId().equals(userId)) {
      throw new ConflictException("У вас нет прав на удаление этого комментария");
    }

    commentRepository.delete(comment);
  }

  /**
   * http://localhost:8080/admin/comments/{commentId}
   * <p> Deletes specified comment
   */
  @Override
  public void delete(final Long commentId) {
    Comment comment = fetchComment(commentId);
    commentRepository.delete(comment);
  }

  /**
   * http://localhost:8080/users/{userId}/comments/{commentId}
   * <p> Update current user's comment
   */
  @Override
  public CommentDto updateUserComment(final Long userId, final Long commentId,
                                      final CommentDto commentDto) {
    Comment comment = fetchComment(commentId);
    fetchUser(userId);

    if (!comment.getUser().getId().equals(userId)) {
      throw new ConflictException("У вас нет прав на редактирование этого комментария");
    }

    comment.setContent(commentDto.getContent());
    Comment updated = commentRepository.save(comment);

    return CommentMapper.mapToCommentDto(updated);
  }

  /**
   * http://localhost:8080/users/{userId}/comments
   * <p> Get all comments created by given user, used for Private API
   */
  @Transactional(readOnly = true)
  @Override
  public List<CommentDto> getAllUserComments(final Long userId) {
    User user = fetchUser(userId);
    return CommentMapper.mapToCommentDto(commentRepository.findByUserId(user));
  }

  /**
   * http://localhost:8080/admin/comments
   * <p> Get comments related specified event, used for Admin API
   */
  @Transactional(readOnly = true)
  @Override
  public List<CommentDto> getAllEventComments(final GetCommentsAdminRequest param) {
    if (!eventRepository.existsById(param.getEventId())) {
      log.warn("Event with ID {} does not exist in the DB.", param.getEventId());
      throw new NotFoundException("Event Not Found.");
    }
    final PageRequest page = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
    final List<Comment> comments =
        commentRepository.findAllByEventId(param.getEventId(), page).getContent();
    return CommentMapper.mapToCommentDto(comments);
  }

  /**
   * http://localhost:8080/events/{eventId}/comments
   * <p> Get comments of given event for public API
   */
  @Transactional(readOnly = true)
  @Override
  public List<CommentDto> getAllEventComments(final Long eventId) {
    Event event = fetchEvent(eventId);
    return CommentMapper.mapToCommentDto(commentRepository.findCommentByEventId(event));
  }

  private User fetchUser(final Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User with ID {} not found.", userId);
          return new NotFoundException("Ошибка нету такого User");
        });
  }

  private Event fetchEvent(final Long eventId) {
    return eventRepository.findById(eventId)
        .orElseThrow(() -> {
          log.warn("Event with ID {} not found.", eventId);
          return new NotFoundException("Ошибка нету такого Event");
        });
  }

  private Comment fetchComment(final Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> {
          log.warn("Comment with ID {} not found.", commentId);
          return new NotFoundException("Комментарий не найден");
        });
  }
}
