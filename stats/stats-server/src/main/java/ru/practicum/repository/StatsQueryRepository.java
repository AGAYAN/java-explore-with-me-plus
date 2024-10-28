package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.ViewStatsDto;

public interface StatsQueryRepository {

  List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
