package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.EndPointHitDto;
import ru.practicum.model.EndPointHit;

@Component
public class StatMapper {

    public static EndPointHit mapToEndpointHit(EndPointHitDto endPointHitDto) {
        EndPointHit endpointHit = new EndPointHit();
        endpointHit.setApp(endPointHitDto.getApp());
        endpointHit.setUri(endPointHitDto.getUrl());
        endpointHit.setIp(endPointHitDto.getIp());
        endpointHit.setTime(endPointHitDto.getTime());
        return endpointHit;
    }

    public static EndPointHitDto mapToEndpointHitDto(EndPointHit endpointHit) {
        EndPointHitDto endpointHitDto = new EndPointHitDto();
        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setUrl(endpointHit.getUri());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setTime(endpointHit.getTime());
        return endpointHitDto;
    }
}
