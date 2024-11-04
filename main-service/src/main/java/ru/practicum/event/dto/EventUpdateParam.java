package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.enums.Role;

@Data
@AllArgsConstructor
public class EventUpdateParam {

  UpdateEventUserRequest updateData;
//  UpdateEventAdminRequest adminUpdateData;
  Long userId;
  Long eventId;
  Role role;

}
