package com.example.service1.controller;

import com.example.service1.entities.customeraccount;
import com.example.service1.entities.orders;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.PurchaseOrderService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/customer")
@Stateful
public class CustomerController {

    @EJB
    private CustomerAccountService customerAccountService;

    @EJB
    private PurchaseOrderService purchaseOrderService;

    private ArrayList<String> cart = new ArrayList<>();

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(customeraccount customer, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            System.out.println("CREATING SHIPPING COMPANY");
            customerAccountService.register(customer.getName(), customer.getUsername(), customer.getPassword(), customer.getLocation());
            session.setAttribute("username", customer.getUsername());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to register Customer").build();
        }
    }

    @POST
    @Path("/login/{username}/{password}")
    public Response loginCustomer(@PathParam("username") String username, @PathParam("password") String password, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (customerAccountService.login(username, password)) {
            session.setAttribute("username", username);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("/viewOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> getAllOrdersForCustomer(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return purchaseOrderService.getAllPurchaseOrders(username);
        } else {
            return null;
        }
    }

    @POST
    @Path("/addProductToCart/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            List<String> cart = (List<String>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            cart.add(name);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add product").build();
        }
    }

    @GET
    @Path("/listCartItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCartItems(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            List<String> cart = (List<String>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            return Response.ok(cart).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }


    @POST
    @Path("/removeProduct/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeProduct(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            List<String> cart = (List<String>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            cart.remove(name);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to remove product").build();
        }
    }

    @GET
    @Path("/clearCart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            List<String> cart = (List<String>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            cart.clear();
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to clear cart").build();
        }
    }






}
