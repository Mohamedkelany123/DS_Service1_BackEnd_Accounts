package com.example.service1.controller;

import com.example.service1.entities.admin;
import com.example.service1.entities.customeraccount;
import com.example.service1.entities.productsellingcompany;
import com.example.service1.entities.shippingcompany;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.ProductSellingCompanyAccountService;
import com.example.service1.services.ShippingCompanyService;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

@Path("/admin")
public class AdminController {

    @EJB
    private ProductSellingCompanyAccountService sellingCompanyService;

    @EJB
    private ShippingCompanyService shippingCompanyService;

    @EJB
    private CustomerAccountService customerAccountService;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(admin admin) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(admin);
            entityManager.getTransaction().commit();
            return Response.status(Status.CREATED).build();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to register admin").build();
        } finally {
            entityManager.close();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginAdmin(admin admin) {
        try {
            admin foundAdmin = entityManager.createQuery("SELECT a FROM admin a WHERE a.username = :username AND a.password = :password", admin.class)
                    .setParameter("username", admin.getUsername())
                    .setParameter("password", admin.getPassword())
                    .getSingleResult();
            if (foundAdmin != null) {
                return Response.status(Status.OK).build();
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("Invalid username or password").build();
            }
        } catch (Exception e) {
            return Response.status(Status.UNAUTHORIZED).entity("Invalid username or password").build();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Path("/listAdmins")
    @Produces(MediaType.APPLICATION_JSON)
    public List<admin> getAllAdmins() {
        TypedQuery<admin> query = entityManager.createQuery("SELECT a FROM admin a", admin.class);
        return query.getResultList();
    }

    @POST
    @Path("/registerSellingCompany/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerProductSellingCompany(@PathParam("name") String company_name) {
        try {
            sellingCompanyService.registerProductSellingCompany(company_name);
            return Response.status(Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to register company").build();
        }
    }

    @GET
    @Path("/listSellingCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<productsellingcompany> getAllProductSellingCompanies() {
        return sellingCompanyService.getAllProductSellingCompanies();
    }

    @POST
    @Path("/registerShippingCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerShippingCompany(shippingcompany company) {
        try {
            System.out.println("CREATING SHIPPING COMPANY");
            shippingCompanyService.createShippingCompany(company.getCompanyName(), company.getGeographicCoverage());
            return Response.status(Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to register shipping company").build();
        }
    }

    @GET
    @Path("/listShippingCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<shippingcompany> getAllShippingCompanies() {
        return shippingCompanyService.listShippingCompanies();
    }

    @DELETE
    @Path("/deleteShippingCompany/{id}")
    public Response deleteShippingCompany(@PathParam("id") Long companyId) {
        try {
            shippingCompanyService.deleteShippingCompany(companyId);
            return Response.status(Status.OK).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to delete shipping company").build();
        }
    }

    @GET
    @Path("/listCustomerAccounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccounts() {
        List<customeraccount> accounts = customerAccountService.getAllCustomerAccounts();
        return Response.ok().entity(accounts).build();
    }
}

