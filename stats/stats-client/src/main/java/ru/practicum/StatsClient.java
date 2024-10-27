package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StatsClient {

    private final WebClient webClient;

    public StatsClient(WebClient.Builder webClientBuilder, @Value("${stats.client.url}") String baseUrl) {
        // Задание базового URL для HTTP-запросов (Docker)
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    // Метод для сохранения информации о посещении
    public void saveHit(EndPointHitDto hit) {
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
                .queryParam("start",  start)
                .queryParam("end",  end)
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