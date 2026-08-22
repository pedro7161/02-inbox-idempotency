package com.example.inventory.service;

import com.example.inventory.event.OrderCreated;
import com.example.inventory.model.InboxEvent;
import com.example.inventory.model.Inventory;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class InventoryEventProcessorTest {

    private static final UUID PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Inject
    InventoryEventProcessor processor;

    @BeforeEach
    void resetDatabase() {
        QuarkusTransaction.requiringNew().run(() -> {
            InboxEvent.deleteAll();
            Inventory.deleteAll();

            Inventory inventory = new Inventory();
            inventory.productId = PRODUCT_ID;
            inventory.availableQuantity = 100;
            inventory.persist();
        });
    }

    @Test
    void firstDeliveryModifiesInventory() {
        OrderCreated event = event(UUID.randomUUID());

        processor.process(event);

        assertEquals(98, currentQuantity());
        assertEquals(1, inboxCount());
    }

    @Test
    void duplicateDeliveryDoesNotModifyInventory() {
        OrderCreated event = event(UUID.randomUUID());

        processor.process(event);
        processor.process(event);

        assertEquals(98, currentQuantity());
        assertEquals(1, inboxCount());
    }

    @Test
    void sameEventFiveTimesCausesOneBusinessOperation() {
        OrderCreated event = event(UUID.randomUUID());

        for (int delivery = 0; delivery < 5; delivery++) {
            processor.process(event);
        }

        assertEquals(98, currentQuantity());
        assertEquals(1, inboxCount());
    }

    @Test
    void differentEventIdsAreProcessedIndependently() {
        processor.process(event(UUID.randomUUID()));
        processor.process(event(UUID.randomUUID()));

        assertEquals(96, currentQuantity());
        assertEquals(2, inboxCount());
    }

    @Test
    void failureRollsBackInventoryAndInbox() {
        OrderCreated event = event(UUID.randomUUID());

        assertThrows(IllegalStateException.class, () -> processor.processWithSimulatedFailure(event));

        assertEquals(100, currentQuantity());
        assertEquals(0, inboxCount());
    }

    private OrderCreated event(UUID eventId) {
        return new OrderCreated(
                eventId,
                "OrderCreated",
                UUID.randomUUID(),
                PRODUCT_ID,
                2,
                Instant.now());
    }

    private int currentQuantity() {
        AtomicInteger quantity = new AtomicInteger();
        QuarkusTransaction.requiringNew().run(() -> {
            Inventory inventory = Inventory.findById(PRODUCT_ID);
            quantity.set(inventory.availableQuantity);
        });
        return quantity.get();
    }

    private long inboxCount() {
        AtomicLong count = new AtomicLong();
        QuarkusTransaction.requiringNew().run(() -> count.set(InboxEvent.count()));
        return count.get();
    }
}
