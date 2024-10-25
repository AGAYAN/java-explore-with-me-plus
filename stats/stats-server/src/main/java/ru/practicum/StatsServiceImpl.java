package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

  private final StatsRepository statsRepository;

  @Override
  @Transactional
  public void saveEndpointHit(final EndPointHitDto dto) {
    log.debug("Persisting a new hit info {}.", dto);
    final EndPointHit hitToSave = StatsMapper.mapToEndPointHit(dto);
    statsRepository.save(hitToSave);
    log.info("EndpointHit saved successfully.");
  }

  @Override
  public List<ViewStatsDto> getStats(final LocalDateTime start, final LocalDateTime end,
                                     final List<String> uris, final boolean unique) {
    log.debug("Retrieving stats for time range: {} - {}, URIs: {}, Unique IPs: {}.",
        start, end, uris, unique);
    validateStartEndDates(start, end);
    final List<ViewStats> result = statsRepository.getStats(start, end, uris, unique);
    return StatsMapper.mapToViewStatsDto(result);
  }

  private void validateStartEndDates(final LocalDateTime start, final LocalDateTime end) {
    if (start == null || end == null) {
      log.warn("Start or end date are/is null.");
      throw new IllegalArgumentException("Start and end date must not be null.");
    }
    if (start.isAfter(end)) {
      log.warn("Start date is after end date.");
      throw new IllegalArgumentException("Start date must be before end date.");
    }
  }
}

