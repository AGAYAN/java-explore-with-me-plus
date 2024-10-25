package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.model.ViewStats;

public interface StatsCustomRepository {

  List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
