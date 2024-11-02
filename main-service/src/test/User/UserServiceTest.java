package User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.user.dto.UserDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import ru.practicum.user.mappers.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private MockedStatic<UserMapper> userMapperMock;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setName("TestName");
        userDto.setEmail("testEmail@mail.ru");

        userMapperMock = Mockito.mockStatic(UserMapper.class);
    }

    @Test
    public void testAddUserSuccess() {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        when(UserMapper.mapToUser(any(UserDto.class))).thenReturn(user);

        when(userRepository.save(any(User.class))).thenReturn(user);

        when(UserMapper.mapToUserDto(any(User.class))).thenReturn(userDto);

        UserDto createUser = userService.addUser(userDto);

        assertNotNull(createUser);
        assertEquals(userDto.getName(), createUser.getName());
        assertEquals(userDto.getEmail(), createUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
