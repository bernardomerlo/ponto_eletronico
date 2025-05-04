package com.bernardomerlo.ponto_eletronico.entities;

import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PunchClock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private PunchType type;
    private LocalDateTime timestamp;

    public PunchClock() {

    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public PunchClock(User user, PunchType type) {
        this.user = user;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public PunchType getType() {
        return type;
    }
}
