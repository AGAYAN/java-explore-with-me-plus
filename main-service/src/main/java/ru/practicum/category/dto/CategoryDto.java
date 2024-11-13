package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CategoryDto {

    @NotBlank
    @NotNull
    private Long id;

    @NotBlank(message = "name should not be blank.")
    @Size(min = 1, max = 50, message = "name should not exceed max length.")
    private String name;
}
