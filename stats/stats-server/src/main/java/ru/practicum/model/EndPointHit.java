package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "endpointhit")
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@Accessors(chain = true)
public class EndPointHit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Integer id;

  @Column(name = "app", nullable = false)
  private String app;

  @Column(name = "uri", nullable = false)
  private String uri;

  @Column(name = "ip", nullable = false, length = 20)
  private String ip;

  @Column(name = "request_time", nullable = false)
  private LocalDateTime requestTime = LocalDateTime.now();
}
