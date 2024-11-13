package ru.practicum.user.mappers;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@UtilityClass
@Slf4j
public class UserMapper {

  public static UserDto mapToUserDto(final User user) {
    log.debug("Mapping User {} to UserDto.", user);
    Objects.requireNonNull(user);
    return new UserDto()
        .setId(user.getId())
        .setName(user.getName())
        .setEmail(user.getEmail());
  }

  public static User mapToUser(final UserDto dto) {
    log.debug("Mapping UserDto {} to User.", dto);
    Objects.requireNonNull(dto);
    return new User()
        .setId(dto.getId())
        .setName(dto.getName())
        .setEmail(dto.getEmail());
  }

  public static UserShortDto mapToUserShortDto(final User user) {
    log.debug("Mapping User {} to UserShortDto.", user);
    Objects.requireNonNull(user);
    return new UserShortDto()
        .setId(user.getId())
        .setName(user.getName());
  }
}