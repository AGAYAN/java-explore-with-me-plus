package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {

    private Long id;

    @NotBlank(message = "name should not be blank.")
    @Length(min = 2, max = 250)
    private String name;

    @NotBlank(message = "email should not be blank.")
    @Length(min = 6, max = 254)
    @Email
    private String email;
}
