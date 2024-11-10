package ru.practicum.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Location {

  @Column(name = "latitude", nullable = false)
  @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90.")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90.")
  private Float lat;

  @Column(name = "longitude", nullable = false)
  @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180.")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180.")
  private Float lon;

}
