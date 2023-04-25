package com.example.service1.controller;

import com.example.service1.entities.admin;
import com.example.service1.entities.product;
import com.example.service1.entities.productsellingcompany;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

//AMA BT3ML STATELESS BEYLOGIN MARA WAHDA BAS
@Path("/sellingCompany")
public class sellingCompanyController {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/login/{company_name}/{password}")
    public Response loginSellingCompany(@PathParam("company_name") String company_name, @PathParam("password") String password) {
        try {
            productsellingcompany foundSellingCompany = entityManager.createQuery("SELECT a FROM productsellingcompany a WHERE a.company_name = :company_name AND a.password = :password", productsellingcompany.class)
                    .setParameter("company_name", company_name)
                    .setParameter("password", password)
                    .getSingleResult();
            if (foundSellingCompany != null) {
                return Response.status(Response.Status.OK).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        } finally {
            entityManager.close();
        }
    }

    @POST
    @Path("/addProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(product product) {
        try {
            System.out.println("product name : "+  product.getName());
            System.out.println("price : "+  product.getPrice());
            System.out.println("seller name : "+  product.getSellerName());
            System.out.println("Quan : "+  product.getQuantity());


            entityManager.getTransaction().begin();
            entityManager.persist(product);
            entityManager.getTransaction().commit();
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add product").build();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Path("/listProducts/{sellerName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<product> getAllAdmins(@PathParam("sellerName") String sellerName) {
        TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p WHERE p.sellerName = :sellerName", product.class);
        query.setParameter("sellerName", sellerName);
        return query.getResultList();
    }





}
