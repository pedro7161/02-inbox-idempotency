package com.example.inventory.service;

import com.example.inventory.event.OrderCreated;
import com.example.inventory.model.InboxEvent;
import com.example.inventory.model.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;

@ApplicationScoped
public class InventoryEventProcessor {

    private static final Logger LOG = Logger.getLogger(InventoryEventProcessor.class);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void process(OrderCreated event) {
        processInsideTransaction(event, false);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void processWithSimulatedFailure(OrderCreated event) {
        processInsideTransaction(event, true);
    }

    private void processInsideTransaction(OrderCreated event, boolean simulateFailure) {
        InboxEvent existingInboxEvent = InboxEvent.findById(event.eventId());
        if (existingInboxEvent != null) {
            LOG.infof("DUPLICATE EVENT IGNORED %s", event.eventId());
            return;
        }

        LOG.infof("PROCESSING NEW EVENT %s", event.eventId());

        Inventory inventory = Inventory.findById(event.productId());
        if (inventory == null) {
            throw new IllegalStateException("Inventory not found for product " + event.productId());
        }
        if (event.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (inventory.availableQuantity < event.quantity()) {
            throw new IllegalStateException("Not enough inventory for product " + event.productId());
        }

        inventory.availableQuantity -= event.quantity();

        if (simulateFailure) {
            throw new IllegalStateException("Simulated failure after inventory modification");
        }

        Instant now = Instant.now();
        InboxEvent inboxEvent = new InboxEvent();
        inboxEvent.eventId = event.eventId();
        inboxEvent.eventType = event.eventType();
        inboxEvent.receivedAt = now;
        inboxEvent.processedAt = now;
        inboxEvent.persist();
    }
}
