package ru.practicum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainServiceApp {

  public static void main(String[] args) {

    ConfigurableApplicationContext context = SpringApplication.run(MainServiceApp.class, args);

    StatsClient statsClient = context.getBean(StatsClient.class);

    final LocalDateTime requestTime = LocalDateTime.now();
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    statsClient.saveHit(new EndPointHitDto()
        .setApp("ewm-main-service")
        .setUri("/events/10")
        .setIp("192.163.0.1")
        .setRequestTime(requestTime)
    );
    statsClient.saveHit(new EndPointHitDto()
        .setApp("ewm-main-service")
        .setUri("/party")
        .setIp("192.163.0.1")
        .setRequestTime(requestTime)
    );
    statsClient.saveHit(new EndPointHitDto()
        .setApp("ewm-main-service")
        .setUri("/events/10")
        .setIp("192.163.0.1")
        .setRequestTime(requestTime)
    );
    statsClient.saveHit(new EndPointHitDto()
        .setApp("ewm-main-service")
        .setUri("/events/10")
        .setIp("192.163.0.1")
        .setRequestTime(requestTime)
    );

    statsClient.getStats(requestTime.minusDays(1L).format(formatter),
        requestTime.plusDays(1L).format(formatter),
        new String[]{"/events/10"}, true);
  }

}
