package ru.practicum.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "location")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(nullable = false)
  @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90.")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90.")
  private Float lat;

  @Column(nullable = false)
  @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180.")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180.")
  private Float lot;

}
