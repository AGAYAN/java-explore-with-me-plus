package ru.practicum.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.enums.State;

@Entity
@Table(name = "event")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(name = "text", length = 2000, nullable = false)
  private String annotation;

  // TODO Добавить как будет доступно
  //  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  //  @JoinColumn(name = "category_id")
  //  private Category category;

  @Column(name = "description", length = 7000, nullable = false)
  private String description;

  @Column(name = "event_date", nullable = false)
  private LocalDateTime eventDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column
  private Boolean paid;

  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;

  // TODO Добавить как будет доступно
  //  @ManyToOne(fetch = FetchType.LAZY)
  //  @JoinColumn(name = "user_id",nullable = false)
  //  private User initiator;

  @Column
  private Integer participantLimit = 0;

  @Column(name = "title", length = 120, nullable = false)
  private String title;

  @Column(name = "published_on")
  private LocalDateTime publishedOn;

  @Column(name = "request_moderation")
  private Boolean requestModeration = true;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private State state;

  @Transient
  private Integer confirmedRequests = 0;

  @Transient
  private Long views;
}
