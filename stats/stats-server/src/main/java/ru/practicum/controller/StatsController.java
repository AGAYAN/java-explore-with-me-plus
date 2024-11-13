package ru.practicum.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndPointHitDto;
import ru.practicum.service.StatsService;
import ru.practicum.ViewStatsDto;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

  private final StatsService statsService;

  @PostMapping("/hit")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveEndpointHit(@Valid @RequestBody final EndPointHitDto endpointHit) {
    log.info("Received request POST /hit with hit info {}", endpointHit.getUri());
    statsService.saveEndpointHit(endpointHit);
  }

  @GetMapping("/stats")
  public ResponseEntity<List<ViewStatsDto>> getStats(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime start,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime end,
      @RequestParam(required = false) final List<String> uris,
      @RequestParam(defaultValue = "false") final boolean unique) {
    log.info("StatService: Received request Get /stats?start={}&end={}&uris={}&unique={}",
        start, end, uris, unique);
    List<ViewStatsDto> resultStats = statsService.getStats(start, end, uris, unique);
    log.info("StatService: Sending statistic result of {} uris.", resultStats.size());
    return ResponseEntity.ok(resultStats);
  }
}
