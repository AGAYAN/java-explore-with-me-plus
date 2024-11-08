package ru.practicum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainServiceApp {

  public static void main(String[] args) {

    SpringApplication.run(MainServiceApp.class, args);
    //ConfigurableApplicationContext context =
    // NOTE: Temporary in using for quick checking main_svc <-> stat connection
  }

}
