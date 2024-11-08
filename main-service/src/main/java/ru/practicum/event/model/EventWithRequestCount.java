package ru.practicum.event.model;

public interface EventWithRequestCount {

  Event getEvent();

  Integer getConfirmedRequests();
}
