package ru.practicum;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;

@UtilityClass
@Slf4j
public class StatsMapper {

  public static EndPointHit mapToEndPointHit(final EndPointHitDto dto) {
    log.debug("Mapping EndPointHitDto {} to EndPointHit.", dto);
    Objects.requireNonNull(dto);
    return new EndPointHit()
        .setApp(dto.getApp())
        .setUri(dto.getUri())
        .setIp(dto.getIp());
  }

  public static List<ViewStatsDto> mapToViewStatsDto(final List<ViewStats> viewStats) {
    if (viewStats == null) {
      return Collections.emptyList();
    }
    return viewStats.stream()
        .map(StatsMapper::mapToViewStatsDto)
        .toList();
  }

  public static ViewStatsDto mapToViewStatsDto(final ViewStats viewStats) {
    log.debug("Mapping viewStats {} to ViewStatsDto.", viewStats);
    Objects.requireNonNull(viewStats);
    return new ViewStatsDto()
        .setApp(viewStats.getApp())
        .setUri(viewStats.getUri())
        .setHits(viewStats.getHits());
  }
}