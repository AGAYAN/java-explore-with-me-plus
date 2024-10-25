package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

  void saveEndpointHit(EndPointHitDto dto);

  List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
