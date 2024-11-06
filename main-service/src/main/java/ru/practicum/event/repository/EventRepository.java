package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

public interface EventRepository extends JpaRepository<Event,Long>, QuerydslPredicateExecutor<Event> {

}
