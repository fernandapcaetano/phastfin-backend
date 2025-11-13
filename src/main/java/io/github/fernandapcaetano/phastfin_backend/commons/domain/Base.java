package io.github.fernandapcaetano.phastfin_backend.commons.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private final UUID externalId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private final ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = true)
    private ZonedDateTime updatedAt;

    private boolean active;

    public Base() {
        this.externalId = UUID.randomUUID();
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = null;
        this.active = true;
    }

    public void disable(){
        if (this.active)
            this.active = false;
    }

    public void enable(){
        if (!this.active)
            this.active = true;
    }

    public Long getId() { return id; }
    public UUID getExternalId() { return externalId; }
    public boolean isActive() { return active; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
}
