package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.EndPointHitDto;
import ru.practicum.ViewStatsDto;

public interface StatsService {

  void saveEndpointHit(EndPointHitDto dto);

  List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
