package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import ru.practicum.exception.StatsBadRequestException;

@Component
@Slf4j
public class StatsClient {

    private final WebClient webClient;

    public StatsClient(WebClient.Builder webClientBuilder, @Value("${stats.client.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void saveHit(EndPointHitDto hit) {
        webClient.post()
                .uri("/hit")
                .bodyValue(hit)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ViewStatsDto[] getStats(String start, String end, String[] uris, boolean unique) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        for (String uriParam : uris) {
            uriBuilder.queryParam("uris", uriParam);
        }

        String uri = uriBuilder.build().toUriString();
        log.debug("Request URI: {}", uri);  // Логируем URL для запроса

        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> Mono.error(new StatsBadRequestException("Bad Request: " + body)));
                                } else {
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> Mono.error(new RuntimeException("Request failed: " + body)));
                                }
                            })
                    .bodyToMono(ViewStatsDto[].class)
                    .block();
        } catch (StatsBadRequestException e) {
            log.error("Bad Request Exception: {}", e.getMessage());
            return new ViewStatsDto[0];
        } catch (Exception e) {
            log.error("Error during request: {}", e.getMessage());
            return new ViewStatsDto[0];
        }
    }
}