package com.example.producer.api;

import com.example.producer.event.OrderCreated;
import com.example.producer.service.OrderEventPublisher;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/events/order-created")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderEventResource {

    @Inject
    OrderEventPublisher publisher;

    @POST
    public Response publish(OrderCreated event) {
        publisher.publish(event);
        return Response.accepted(Map.of(
                "eventId", event.eventId(),
                "publishedCount", 1)).build();
    }

    @POST
    @Path("/repeat")
    public Response publishRepeated(OrderCreated event, @QueryParam("times") @DefaultValue("5") int times) {
        if (times < 1 || times > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "times must be between 1 and 100"))
                    .build();
        }

        publisher.publishRepeated(event, times);
        return Response.accepted(Map.of(
                "eventId", event.eventId(),
                "publishedCount", times,
                "sameEventInstance", true)).build();
    }
}
