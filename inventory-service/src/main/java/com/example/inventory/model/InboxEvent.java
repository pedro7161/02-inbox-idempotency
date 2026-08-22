package com.example.inventory.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox_event")
public class InboxEvent extends PanacheEntityBase {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    public UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    public String eventType;

    @Column(name = "received_at", nullable = false)
    public Instant receivedAt;

    @Column(name = "processed_at", nullable = false)
    public Instant processedAt;
}
