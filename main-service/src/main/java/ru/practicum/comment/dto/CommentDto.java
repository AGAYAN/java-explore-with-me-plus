package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @NotBlank
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
