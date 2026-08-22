package com.example.inventory.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "inventory")
public class Inventory extends PanacheEntityBase {

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    public UUID productId;

    @Column(name = "available_quantity", nullable = false)
    public int availableQuantity;
}
