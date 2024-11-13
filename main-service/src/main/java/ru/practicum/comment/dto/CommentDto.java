package ru.practicum.comment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private String content;
}
