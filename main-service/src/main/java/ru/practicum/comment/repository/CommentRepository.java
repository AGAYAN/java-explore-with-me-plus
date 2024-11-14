package ru.practicum.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByUserId(Long userId);

  Page<Comment> findAllByEventId(Long eventId, PageRequest page);
}
