package com.example.producer.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCreated(
        UUID eventId,
        String eventType,
        UUID orderId,
        UUID productId,
        int quantity,
        Instant occurredAt) {
}
