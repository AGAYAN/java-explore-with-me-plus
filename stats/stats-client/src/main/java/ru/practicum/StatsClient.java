package ru.practicum;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class StatsClient {

    private final WebClient webClient;

    public StatsClient(WebClient.Builder webClientBuilder) {
        // Задание базового URL для HTTP-запросов (Docker)
        String baseUrl = "http://stats-server:9090";
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    // Метод для сохранения информации о посещении
    public void saveHit(EndpointHitDto hit) {
        webClient.post()
                .uri("/hit")
                .bodyValue(hit)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // Метод для получения статистики по посещениям
    public ViewStatsDto[] getStats(String start, String end, String[] uris, boolean unique) {
        String uri = UriComponentsBuilder.fromUriString("/stats")
                .queryParam("start", URLEncoder.encode(start, StandardCharsets.UTF_8))
                .queryParam("end", URLEncoder.encode(end, StandardCharsets.UTF_8))
                .queryParam("uris", uris) // Отправляем массив как параметр
                .queryParam("unique", unique)
                .build()
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ViewStatsDto[].class)
                .block();
    }
}