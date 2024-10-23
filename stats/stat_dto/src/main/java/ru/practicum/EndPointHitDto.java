package ru.practicum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NotBlank
@Getter
@Setter
@RequiredArgsConstructor
public class EndPointHitDto {
    private String app;
    private String url;
    private String ip;
    private LocalDateTime time;
}
