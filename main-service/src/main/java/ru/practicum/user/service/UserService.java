package ru.practicum.user.service;

import java.util.List;
import ru.practicum.user.dto.UserDto;

public interface UserService {

  UserDto addUser(UserDto userDto);

  void deleteUser(Long userId);

  List<UserDto> getUsers(List<Long> ids, int from, int size);

  UserDto getUser(Long userId);

  void validateUserExist(final Long id);

}
