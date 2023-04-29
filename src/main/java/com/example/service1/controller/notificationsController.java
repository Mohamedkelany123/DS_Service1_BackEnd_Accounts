package com.example.service1.controller;

import com.example.service1.services.CustomerAccountService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/notifications")
public class notificationsController {
    @EJB
    private CustomerAccountService customerAccountService;

    @POST
    @Path("/sendNotification/{message}/{customerName}/{orderId}")
    public Response sendNotification(@PathParam("message") String message, @PathParam("customerName") String customerName, @PathParam("orderId") Long orderId){
        try {
            customerAccountService.sendToQueue(message, customerName, orderId);
            return Response.status(Response.Status.CREATED).build();
        }catch (Exception e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to send notification").build();
        }
    }
}
