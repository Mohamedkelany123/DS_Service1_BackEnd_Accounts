package com.example.service1.controller;

import com.example.service1.entities.orders;
import com.example.service1.entities.sellingcompanysoldproducts;
import com.example.service1.services.CustomerAccountService;
import jakarta.ejb.EJB;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/shippingEndPoint")
public class shippingEndPoints {
    @EJB
    private CustomerAccountService customerAccountService;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

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

    @GET
    @Path("/listCompatiblePendingOrders/{location}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> listCompatiblePendingOrders(@PathParam("location") String location) {
            TypedQuery<orders> query = entityManager.createQuery("SELECT p FROM orders p WHERE p.location = :location AND p.status = 'pending'", orders.class);
            query.setParameter("location", location);
            return query.getResultList();
    }

    @POST
    @Path("/processOrderById/{id}/{company_name}")
    public Response processOrderById(@PathParam("id") int id,@PathParam("company_name") String company_name){
        int rowsUpdated = 0;
        try {
            entityManager.getTransaction().begin();

            rowsUpdated = entityManager.createQuery("UPDATE orders p SET p.status = 'processing' WHERE p.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            entityManager.createQuery("UPDATE orders p SET p.shipping_company = :company_name WHERE p.id = :id")
                    .setParameter("id", id)
                    .setParameter("company_name", company_name)
                    .executeUpdate();
            entityManager.createQuery("UPDATE sellingcompanysoldproducts p SET p.status = 'processing' WHERE p.orderId = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            entityManager.createQuery("UPDATE sellingcompanysoldproducts p SET p.shipping_company = :company_name WHERE p.orderId = :id")
                    .setParameter("id", id)
                    .setParameter("company_name", company_name)
                    .executeUpdate();

            entityManager.getTransaction().commit();

            Query query = entityManager.createQuery("SELECT p.customerName FROM orders p WHERE p.id = :id");
            query.setParameter("id", id);
            String username = (String) query.getSingleResult();

            String msg = "Order Id[" + id +"] is processing now by company ["+ company_name+ "]";
            System.out.println("msg: " + msg);
            System.out.println("Username: " + username);
            System.out.println("id: " + id);

            sendNotification(msg, username, (long) id);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        if(rowsUpdated != 0){
            //customerAccountService.sendToQueue("Order Id[" + id +"] is processing now by company ["+ company_name+ "]", username, (long) id);
            return Response.status(Response.Status.CREATED).entity("Order Processed Successfully!").build();
        }else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to process order").build();
        }
    }

    @GET
    @Path("/listProcessingOrders/{company_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> listProcessingOrders(@PathParam("company_name") String company_name) {
        TypedQuery<orders> query = entityManager.createQuery("SELECT p FROM orders p WHERE p.shipping_company = :company_name AND p.status = 'processing'", orders.class);
        query.setParameter("company_name", company_name);
        return query.getResultList();
    }

    @POST
    @Path("/setShippedById/{id}/{company_name}")
    public Response setShippedById(@PathParam("id") int id,@PathParam("company_name") String company_name){
        int rowsUpdated = 0;
        try {
            entityManager.getTransaction().begin();

            rowsUpdated = entityManager.createQuery("UPDATE orders p SET p.status = 'shipped' WHERE p.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            entityManager.createQuery("UPDATE sellingcompanysoldproducts p SET p.status = 'shipped' WHERE p.orderId = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            entityManager.getTransaction().commit();

            Query query = entityManager.createQuery("SELECT p.customerName FROM orders p WHERE p.id = :id");
            query.setParameter("id", id);
            String username = (String) query.getSingleResult();

            String msg = "Order Id[" + id +"] is shipped successfully!";
            System.out.println("msg: " + msg);
            System.out.println("Username: " + username);
            System.out.println("id: " + id);

            sendNotification(msg, username, (long) id);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        if(rowsUpdated != 0){
            //customerAccountService.sendToQueue("Order Id[" + id +"] is processing now by company ["+ company_name+ "]", username, (long) id);
            return Response.status(Response.Status.CREATED).entity("Order Shipped Successfully!").build();
        }else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to ship order").build();
        }
    }

    @GET
    @Path("/listShippedOrders/{company_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> listShippedOrders(@PathParam("company_name") String company_name) {
        TypedQuery<orders> query = entityManager.createQuery("SELECT p FROM orders p WHERE p.shipping_company = :company_name AND p.status = 'shipped'", orders.class);
        query.setParameter("company_name", company_name);
        return query.getResultList();
    }


}

