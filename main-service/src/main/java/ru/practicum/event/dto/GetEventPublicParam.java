package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.enums.SortType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetEventPublicParam {

    @Size(max = 2000, message = "Search text must be up to 2000 characters.")
    private String text;
    private List<@Positive(message = "Category ID must be a positive number.")Long> categories;
    private Boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable = false;
    private SortType sort;
    @Min(value = 0, message = "Offset 'from' must be zero or positive.")
    private Integer from;
    @Positive(message = "Page size 'size' must be a positive number.")
    private Integer size;

}
