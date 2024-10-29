package ru.practicum.mapper;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.EndPointHitDto;
import ru.practicum.model.EndPointHit;

@UtilityClass
@Slf4j
public class StatsMapper {

  public static EndPointHit mapToEndPointHit(final EndPointHitDto dto) {
    log.debug("Mapping EndPointHitDto {} to EndPointHit.", dto);
    Objects.requireNonNull(dto);
    return new EndPointHit()
        .setApp(dto.getApp())
        .setUri(dto.getUri())
        .setIp(dto.getIp())
        .setRequestTime(dto.getRequestTime());
  }

}