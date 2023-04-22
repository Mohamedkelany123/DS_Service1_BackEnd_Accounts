package com.example.service1.controller;

import com.example.service1.entities.admin;
import com.example.service1.entities.customeraccount;
import com.example.service1.services.CustomerAccountService;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customer")
public class CustomerController {

    @EJB
    private CustomerAccountService customerAccountService;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(customeraccount customer) {
        try {
            System.out.println("CREATING SHIPPING COMPANY");
            customerAccountService.register(customer.getName(), customer.getUsername(), customer.getPassword());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to register Customer").build();
        }
    }

    @POST
    @Path("/login/{username}/{password}")
    public Response loginAdmin(@PathParam("username") String username, @PathParam("password") String password) {
        if (customerAccountService.login(username, password)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}