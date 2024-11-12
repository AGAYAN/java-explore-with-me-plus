package ru.practicum;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.controller.StatsController;
import ru.practicum.service.StatsService;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  StatsService statsService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @SneakyThrows
  @Test
  void saveEndpointHit_whenValidInput_thenCreated() {
    final EndPointHitDto endpointHit = buildEndpointHitDto();
    doNothing().when(statsService).saveEndpointHit(any(EndPointHitDto.class));

    mvc.perform(post("/hit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(endpointHit)))
        .andExpect(status().isCreated());

    verify(statsService, times(1))
        .saveEndpointHit(any(EndPointHitDto.class));
    ;
    verifyNoMoreInteractions(statsService);
  }

  @SneakyThrows
  @Test
  @DisplayName("getStats() with all four valid parameters")
  void getStats_whenAllValidParameterArePassed_thenOkWithContent() {
    final LocalDateTime start = LocalDateTime.now().minusDays(3);
    final LocalDateTime end = LocalDateTime.now().minusDays(1);
    final List<String> uris = List.of("/events/1", "/events/2");
    final boolean unique = false;

    final ViewStatsDto viewStats = buildViewStatsDto();
    final List<ViewStatsDto> searchResult = List.of(viewStats);

    when(statsService.getStats(
        any(LocalDateTime.class), any(LocalDateTime.class),
        anyList(), anyBoolean()))
        .thenReturn(searchResult);

    mvc.perform(get("/stats")
            .param("start", start.format(formatter))
            .param("end", end.format(formatter))
            .param("uris", uris.toArray(new String[0]))
            .param("unique", String.valueOf(unique)))

        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].app").value(viewStats.getApp()))
        .andExpect(jsonPath("$[0].uri").value(viewStats.getUri()))
        .andExpect(jsonPath("$[0].hits").value(viewStats.getHits()));

    verify(statsService, times(1))
        .getStats(start, end, uris, unique);

    verifyNoMoreInteractions(statsService);
  }


  @SneakyThrows
  @Test
  @DisplayName("getStats() with valid parameters, unique and uris are not passed")
  void getStats_whenOnlyStartAndEndArePassed_thenOkWithContent() {
    final LocalDateTime start = LocalDateTime.now().minusDays(3);
    final LocalDateTime end = LocalDateTime.now().minusDays(1);

    final ViewStatsDto viewStats = buildViewStatsDto();
    final List<ViewStatsDto> searchResult = List.of(viewStats);

    when(statsService.getStats(
        any(LocalDateTime.class), any(LocalDateTime.class), isNull(), any(Boolean.class)))
        .thenReturn(searchResult);

    mvc.perform(get("/stats")
            .param("start", start.format(formatter))
            .param("end", end.format(formatter)))

        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].app").value(viewStats.getApp()))
        .andExpect(jsonPath("$[0].uri").value(viewStats.getUri()))
        .andExpect(jsonPath("$[0].hits").value(viewStats.getHits()));

    verify(statsService, times(1))
        .getStats(eq(start), eq(end), isNull(), eq(false));

    verifyNoMoreInteractions(statsService);
  }

  @SneakyThrows
  @Test
  @DisplayName("getStats() with valid parameters uris are not passed")
  void getStats_whenUrisAreNotPassed_thenOkWithContent() {
    final LocalDateTime start = LocalDateTime.now().minusDays(3);
    final LocalDateTime end = LocalDateTime.now().minusDays(1);
    final boolean unique = true;

    final ViewStatsDto viewStats = buildViewStatsDto();
    final List<ViewStatsDto> searchResult = List.of(viewStats);

    when(statsService.getStats(
        any(LocalDateTime.class), any(LocalDateTime.class), isNull(), any(Boolean.class)))
        .thenReturn(searchResult);

    mvc.perform(get("/stats")
            .param("start", start.format(formatter))
            .param("end", end.format(formatter))
            .param("unique", String.valueOf(unique)))

        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].app").value(viewStats.getApp()))
        .andExpect(jsonPath("$[0].uri").value(viewStats.getUri()))
        .andExpect(jsonPath("$[0].hits").value(viewStats.getHits()));

    verify(statsService, times(1))
        .getStats(eq(start), eq(end), isNull(), eq(true));

    verifyNoMoreInteractions(statsService);
  }

  @SneakyThrows
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideInvalidEndpointHitDto")
  void saveEndpointHit_whenInvalidParameters(final String testName,
                                             final EndPointHitDto endPointHitDto) {
    mvc.perform(post("/hit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(endPointHitDto)))
        .andExpect(status().isInternalServerError());

    verifyNoInteractions(statsService);

  }

  private static Stream<Arguments> provideInvalidEndpointHitDto() {
    return Stream.of(
        Arguments.of("app- null", buildEndpointHitDto().setApp(null)),
        Arguments.of("app- blank", buildEndpointHitDto().setApp("  ")),
        Arguments.of("app- length 256",
            buildEndpointHitDto().setApp("app is too long.".repeat(16))),
        Arguments.of("uri- null", buildEndpointHitDto().setUri(null)),
        Arguments.of("uri- blank", buildEndpointHitDto().setUri("  ")),
        Arguments.of("uri- length 256",
            buildEndpointHitDto().setUri("uri is too long.".repeat(16))),
        Arguments.of("ip- null", buildEndpointHitDto().setIp(null)),
        Arguments.of("ip- blank", buildEndpointHitDto().setIp(" ")),
        Arguments.of("ip- length 40",
            buildEndpointHitDto().setIp("234.1".repeat(8))),
        Arguments.of("requestTime- null",
            buildEndpointHitDto().setRequestTime(null)),
        Arguments.of("requestTime- in future",
            buildEndpointHitDto().setRequestTime(LocalDateTime.now().plusMinutes(5))));

  }


  @SneakyThrows
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideMissingDatesFor_getStats")
  void getStats_whenInvalidParameters(final String testName,
                                      final LocalDateTime start,
                                      final LocalDateTime end) {
    final List<String> uris = List.of("/events/1", "/events/2");
    final boolean unique = false;
    MockHttpServletRequestBuilder requestBuilder = get("/stats")
        .param("uris", uris.toArray(new String[0]))
        .param("unique", String.valueOf(unique));
    if (start != null) {
      requestBuilder.param("start", start.format(formatter));
    }
    if (end != null) {
      requestBuilder.param("end", end.format(formatter));
    }

    mvc.perform(requestBuilder)
        .andExpect(status().isBadRequest());

    verifyNoInteractions(statsService);

  }

  private static Stream<Arguments> provideMissingDatesFor_getStats() {
    return Stream.of(
        Arguments.of("Start and End are missing", null, null),

        Arguments.of("Start is missing", null, LocalDateTime.now().minusDays(1)),

        Arguments.of("End is missing", LocalDateTime.now().minusDays(3), null));
  }

  @SneakyThrows
  @Test
  void getStats_whenInvalidDateFormat_thenThrow() {
    final String start = "2024-25-10 15:00:00";
    final LocalDateTime end = LocalDateTime.now().minusDays(1);
    final List<String> uris = List.of("/events/1", "/events/2");
    final boolean unique = false;

    MockHttpServletRequestBuilder requestBuilder = get("/stats")
        .param("start", start)
        .param("end", end.format(formatter))
        .param("uris", uris.toArray(new String[0]))
        .param("unique", String.valueOf(unique));

    mvc.perform(requestBuilder)
        .andExpect(status().isInternalServerError());

    verifyNoInteractions(statsService);
  }

  @SneakyThrows
  @Test
  void getStats_whenInvalidUniqueValue_thenThrow() {
    final LocalDateTime start = LocalDateTime.now().minusDays(3);
    final LocalDateTime end = LocalDateTime.now().minusDays(1);
    final List<String> uris = List.of("/events/1", "/events/2");
    final boolean unique = false;

    MockHttpServletRequestBuilder requestBuilder = get("/stats")
        .param("start", start.format(formatter))
        .param("end", end.format(formatter))
        .param("uris", uris.toArray(new String[0]))
        .param("unique", "unique");

    mvc.perform(requestBuilder)
        .andExpect(status().isInternalServerError());

    verifyNoInteractions(statsService);
  }

  private ViewStatsDto buildViewStatsDto() {
    return new ViewStatsDto("ewm-main-service","/events/1",10L);
  }

  private static EndPointHitDto buildEndpointHitDto() {
    return new EndPointHitDto()
        .setApp("ewm-main-service")
        .setUri("/events/1")
        .setIp("192.163.0.1")
        .setRequestTime(LocalDateTime.now());
  }

}