package com.example.inventory.messaging;

import com.example.inventory.event.OrderCreated;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderCreatedDeserializer extends ObjectMapperDeserializer<OrderCreated> {

    public OrderCreatedDeserializer() {
        super(OrderCreated.class);
    }
}
