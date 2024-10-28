package ru.practicum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class ViewStatsDto {

    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @PositiveOrZero
    private Long hits;
}
