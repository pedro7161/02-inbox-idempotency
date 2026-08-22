package com.example.inventory.messaging;

import com.example.inventory.event.OrderCreated;
import com.example.inventory.service.InventoryEventProcessor;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@UnlessBuildProfile("test")
public class OrderCreatedConsumer {

    @Inject
    InventoryEventProcessor processor;

    @Incoming("order-created")
    public void consume(OrderCreated event) {
        processor.process(event);
    }
}
