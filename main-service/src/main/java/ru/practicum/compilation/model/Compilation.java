package ru.practicum.compilation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.event.model.Event;

@Entity
@Table(name = "compilation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Compilation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, unique = true)
  private String title;

  @Column(name = "pinned", nullable = false, columnDefinition = "boolean default false")
  private boolean pinned;

  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(name = "compilation_event",
      joinColumns = @JoinColumn(name = "compilation_id"),
      inverseJoinColumns = @JoinColumn(name = "event_id"))
  private Set<Event> events;
}
