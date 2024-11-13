package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * http://localhost:8080/users/1/comments?eventId=11
     * Saves a new comment data initiated by a current user.
     */
    @Override
    public CommentDto addComment(CommentDto commentDto) {
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Ошибка нету такого User"));

        Event event = eventRepository.findById(commentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Ошибка нету такого Event"));

        Comment comment = CommentMapper.mapTo(commentDto, user, event);

        comment.setCreated(LocalDateTime.now());

        if (user.getId().equals(event.getInitiator().getId())) {
            comment.setInitiator(true);
        }

        Comment saveComment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(saveComment);

    }

    /**
     * http://localhost:8080/users/1/comments/1
     * delete user comment
     */
    @Override
    public void deleteUserComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("У вас нет прав на удаление этого комментария");
        }

        commentRepository.delete(comment);
    }

    /**
     * http://localhost:8080/users/2/comments/2
     * update user comment
     */
    @Override
    public CommentDto updateUserComment(Long userId, Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException("Ошибка нету такого User"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("У вас нет прав на удаление этого комментария");
        }

        comment.setContent(commentDto.getContent());
        Comment update = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(update);
    }

    /**
     * http://localhost:8080/events/11/comments
     * EventID comment to user
     */
    @Override
    public List<Comment> getAllEventComments(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException("Ошибка нету такого Event"));

        return commentRepository.findCommentByEventId(event);
    }

    /**
     * http://localhost:8080/users/2/comments
     * get userId comment
     */
    @Override
    public List<Comment> getByUserComment(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException("Ошибка нету такого User"));

        return commentRepository.findByUserId(user);
    }
}