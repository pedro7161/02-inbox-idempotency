package com.example.inventory.api;

import com.example.inventory.model.InboxEvent;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/inbox")
@Produces(MediaType.APPLICATION_JSON)
public class InboxResource {

    @GET
    public List<InboxEvent> list() {
        return InboxEvent.list("order by receivedAt desc");
    }

    @GET
    @Path("/{eventId}")
    public InboxEvent get(@PathParam("eventId") UUID eventId) {
        InboxEvent inboxEvent = InboxEvent.findById(eventId);
        if (inboxEvent == null) {
            throw new NotFoundException();
        }
        return inboxEvent;
    }
}
