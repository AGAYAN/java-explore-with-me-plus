package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import ru.practicum.exception.StatsBadRequestException;

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
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        // Добавляем каждый URI как отдельный параметр
        for (String uriParam : uris) {
            uriBuilder.queryParam("uris", uriParam);
        }

        String uri = uriBuilder.build().toUriString();
        System.out.println("Request URI: " + uri);  // Логируем URL для запроса

        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(
                            // Обработка ошибки 4xx и 5xx
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                                    // Если ошибка 400, выбрасываем кастомное исключение StatsBadRequestException
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> Mono.error(new StatsBadRequestException("Bad Request: " + body)));
                                } else {
                                    // Обработка других ошибок
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> Mono.error(new RuntimeException("Request failed: " + body)));
                                }
                            })
                    .bodyToMono(ViewStatsDto[].class)
                    .block();
        } catch (StatsBadRequestException e) {
            // Ловим исключение StatsBadRequestException, которое выбрасывается в случае ошибки 400
            System.out.println("Bad Request Exception: " + e.getMessage());
            throw e; // Прокидываем исключение дальше
        } catch (Exception e) {
            // Ловим другие исключения
            System.out.println("Error during request: " + e.getMessage());
            throw new RuntimeException("Error while fetching stats: " + e.getMessage(), e);
        }
    }
//    public ViewStatsDto[] getStats(String start, String end, String[] uris, boolean unique) {
//        String uri = UriComponentsBuilder.fromUriString("/stats")
//                .queryParam("start",  start)
//                .queryParam("end",  end)
//                .queryParam("uris", uris) // Отправляем массив как параметр
//                .queryParam("unique", unique)
//                .build()
//                .toUriString();
//
//        return webClient.get()
//                .uri(uri)
//                .retrieve()
//                .bodyToMono(ViewStatsDto[].class)
//                .block();
//    }
}