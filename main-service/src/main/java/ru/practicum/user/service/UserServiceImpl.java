package ru.practicum.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mappers.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers(String ids, int from, int size) {
        List<Long> idList = null;

        if (!ids.equals("null")) {
            idList = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(from, size);
        Page<User> users;

        if (idList != null && !idList.isEmpty()) {
            users = userRepository.findAllByIdIn(idList, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.getContent().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}