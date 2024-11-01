package ru.practicum.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserShortDto {

  private Long id;
  private String name;

}
