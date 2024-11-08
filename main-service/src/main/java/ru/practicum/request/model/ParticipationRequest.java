package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request")
@EqualsAndHashCode(of = "id")
@Accessors(chain = true)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private StatusRequest status = StatusRequest.PENDING;

    private LocalDateTime created = LocalDateTime.now();

    public ParticipationRequest(User user, Event event) {
        this.requester = user;
        this.event = event;
        this.status = StatusRequest.PENDING;
    }
}
