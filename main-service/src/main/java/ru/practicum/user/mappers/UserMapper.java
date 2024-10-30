package ru.practicum.user.mappers;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.Objects;

@UtilityClass
@Slf4j
public class UserMapper {

    public static UserDto mapToUserDto(final User user) {
        log.debug("Mapping User {} to UserDto.", user);
        Objects.requireNonNull(user);
        return new UserDto()
                .setName(user.getName())
                .setEmail(user.getEmail());
    }

    public static User mapToUser(final UserDto dto) {
        log.debug("Mapping UserDto {} to User.", dto);
        Objects.requireNonNull(dto);
        return new User()
                .setName(dto.getName())
                .setEmail(dto.getEmail());
    }
}