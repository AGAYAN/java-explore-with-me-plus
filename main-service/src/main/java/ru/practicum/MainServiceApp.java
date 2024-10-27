package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainServiceApp {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(MainServiceApp.class, args);
    StatsClient statsClient = context.getBean(StatsClient.class);
    statsClient.saveHit(new EndPointHitDto());
  }

}
