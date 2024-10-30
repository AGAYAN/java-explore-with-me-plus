package User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ru.practicum.user.mappers.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("johndoe@example.com");
    }

    @Test
    public void testAddUserSuccess() {

        User user = UserMapper.mapToUser(userDto);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto createUser = userService.addUser(userDto);

        assertNotNull(createUser);
        assertEquals(userDto.getName(), createUser.getName());
        assertEquals(userDto.getEmail(), createUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));

    }
}
