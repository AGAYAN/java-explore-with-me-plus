package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndPointHit;
import ru.practicum.repository.StatRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService{

    private final StatRepository statRepository;

    @Override
    public void addEndPointHit(EndPointHit endpointHit) {
        statRepository.save(endpointHit);
        log.info("test", endpointHit);
    }
}
