package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CategoryDto {

    @NotBlank
    @NotNull
    private Long id;

    @NotBlank(message = "name should not be blank.")
    @Size(min = 1, max = 50, message = "name should not exceed max length.")
    private String name;
}
