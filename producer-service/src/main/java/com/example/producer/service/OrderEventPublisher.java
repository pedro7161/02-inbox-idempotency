package com.example.producer.service;

import com.example.producer.event.OrderCreated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderEventPublisher {

    @Inject
    @Channel("order-created")
    Emitter<OrderCreated> emitter;

    public void publish(OrderCreated event) {
        emitter.send(event).toCompletableFuture().join();
    }

    public void publishRepeated(OrderCreated event, int times) {
        for (int delivery = 0; delivery < times; delivery++) {
            publish(event);
        }
    }
}
