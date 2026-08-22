package com.example.inventory.api;

import com.example.inventory.model.Inventory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @GET
    @Path("/{productId}")
    public Inventory get(@PathParam("productId") UUID productId) {
        Inventory inventory = Inventory.findById(productId);
        if (inventory == null) {
            throw new NotFoundException();
        }
        return inventory;
    }
}
