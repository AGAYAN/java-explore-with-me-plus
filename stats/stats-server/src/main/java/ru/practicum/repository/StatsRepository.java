package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EndPointHit;

public interface StatsRepository extends JpaRepository<EndPointHit, Integer>, StatsQueryRepository {

}
