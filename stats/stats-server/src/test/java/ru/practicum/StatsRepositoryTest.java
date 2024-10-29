package ru.practicum;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EndPointHit;
import ru.practicum.repository.StatsRepository;

@DataJpaTest
@Transactional
class StatsRepositoryTest {

  @Autowired
  private StatsRepository statsRepository;

  private static LocalDateTime now = LocalDateTime.now();
  private static EndPointHit hitOne = buildEndpointHit("ewm-main-service", "/events/1", "192.163.0.1", now);
  private static EndPointHit hitTwo = buildEndpointHit("ewm-main-service", "/events/1", "192.163.0.1", now.minusDays(2));
  private static EndPointHit hitThree = buildEndpointHit("ewm-main-service", "/events/5", "120.132.0.7", now.minusDays(5));

  @BeforeEach
  void setUp() {
    statsRepository.deleteAll();
  }

  @Test
  void save_shouldPersistEndPointHitToDatabase() {

    statsRepository.save(hitOne);
    final List<EndPointHit> actualHitsInDb = statsRepository.findAll();

    assertThat(actualHitsInDb).hasSize(1);
    final EndPointHit savedHit = actualHitsInDb.getFirst();
    assertThat(savedHit.getApp()).isEqualTo(hitOne.getApp());
    assertThat(savedHit.getUri()).isEqualTo(hitOne.getUri());
    assertThat(savedHit.getIp()).isEqualTo(hitOne.getIp());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("getStatsParametersProvider")
  void getStats_shouldReturnStatsWithCorrectCounts(final String testName, final LocalDateTime start,
                              final LocalDateTime end,
                              final List<String> uris, boolean unique,
                              final List<ViewStatsDto> expected) {
    statsRepository.save(hitOne);// now
    statsRepository.save(hitTwo); // now - 2d
    statsRepository.save(hitThree); // now - 5d

    final List<ViewStatsDto> actual = statsRepository.getStats(start, end, uris, unique);

    assertThat(actual).hasSize(expected.size());
    ViewStatsDto stats = actual.getFirst();
    assertThat(stats.getApp()).isEqualTo(expected.getFirst().getApp());
    assertThat(stats.getUri()).isEqualTo(expected.getFirst().getUri());
    assertThat(stats.getHits()).isEqualTo(expected.getFirst().getHits()); // non-unique IPs
    if (expected.size() > 1) {
      assertThat(actual.getLast().getApp()).isEqualTo(expected.getLast().getApp());
      assertThat(actual.getLast().getUri()).isEqualTo(expected.getLast().getUri());
      assertThat(actual.getLast().getHits()).isEqualTo(expected.getLast().getHits());
    }
  }

  private static Stream<Arguments> getStatsParametersProvider() {
    LocalDateTime now = LocalDateTime.now();
    return Stream.of(
        Arguments.of("One uri, not unique ip",
            now.minusDays(3), now.plusDays(1), List.of("/events/1"), false,
            List.of(new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 2L))
        ),

        Arguments.of("One uri, unique ip",
            now.minusDays(3), now.plusDays(1), List.of("/events/1"),true,
            List.of(new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 1L))),

        Arguments.of("Multiple uri, not unique ip",
            now.minusDays(6),now.plusDays(1),List.of("/events/1", "/events/5"), false,
            List.of(
                new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 2L),
                new ViewStatsDto(hitThree.getApp(), hitThree.getUri(), 1L))),


        Arguments.of("Multiple uri,  unique ip",
            now.minusDays(6),now.plusDays(1),List.of("/events/1", "/events/5"), true,
            List.of(
                new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 1L),
                new ViewStatsDto(hitThree.getApp(), hitThree.getUri(), 1L))),

        Arguments.of("Null uri, not unique ip",
            now.minusDays(6),now.plusDays(1), null, false,
            List.of(
                new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 2L),
                new ViewStatsDto(hitThree.getApp(), hitThree.getUri(), 1L))),


        Arguments.of("Null uri, unique ip",
            now.minusDays(6),now.plusDays(1), null, true,
            List.of(
                new ViewStatsDto(hitOne.getApp(), hitOne.getUri(), 1L),
                new ViewStatsDto(hitThree.getApp(), hitThree.getUri(), 1L)))
    );
  }

  private static  EndPointHit buildEndpointHit(final String app, final String uri, final String ip,
                                       final LocalDateTime requestTime) {
    return new EndPointHit()
        .setApp(app)
        .setUri(uri)
        .setIp(ip)
        .setRequestTime(requestTime);
  }

}