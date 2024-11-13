package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NotBlank
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private boolean isInitiator;
    @Size(min=1, max=5000)
    private String content;
    private LocalDateTime created;
}
