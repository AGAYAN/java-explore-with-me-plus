package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.enums.Role;

@Data
@AllArgsConstructor
public class EventUpdateParam {

  private UpdateEventUserRequest updateData;
  private Long userId;
  private Long eventId;
  private Role role;

}
