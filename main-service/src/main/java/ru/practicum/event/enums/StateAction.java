package ru.practicum.event.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum StateAction {

  PUBLISH_EVENT(Role.ADMIN, State.PUBLISHED),
  REJECT_EVENT(Role.ADMIN,State.CANCELED),

  SEND_TO_REVIEW(Role.USER,State.PENDING),
  CANCEL_REVIEW(Role.USER,State.CANCELED);

  private final Role role;
  private final State state;


  public static StateAction fromString(final String stateAction) {
    Objects.requireNonNull(stateAction, "Invalid stateAction value.");
    for (StateAction sa : StateAction.values()) {
      if (sa.name().equalsIgnoreCase(stateAction.trim())) {
        return sa;
      }
    }
    log.warn("Invalid stateAction value: {}", stateAction);
    throw new IllegalArgumentException("Invalid state value.");
  }

  public static List<String> getValidStates(final Role role) {
    return Arrays.stream(values())
        .filter(sa -> sa.getRole().equals(role))
        .map(Enum::name)
        .toList();
  }
}
