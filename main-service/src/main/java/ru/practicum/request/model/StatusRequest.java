package ru.practicum.request.model;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum StatusRequest {
  CONFIRMED,
  PENDING,
  REJECTED,
  CANCELED;

  public static StatusRequest fromString(final String statusRequest) {
    Objects.requireNonNull(statusRequest, "Invalid statusRequest value.");
    for (StatusRequest status : StatusRequest.values()) {
      if (status.name().equalsIgnoreCase(statusRequest.trim())) {
        return status;
      }
    }
    log.warn("Invalid statusRequest value: {}", statusRequest);
    throw new IllegalArgumentException("Invalid status of the participation request value.");
  }
}
