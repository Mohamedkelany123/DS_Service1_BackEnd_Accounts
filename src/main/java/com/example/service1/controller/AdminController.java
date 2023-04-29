package com.example.service1.controller;

import com.example.service1.entities.admin;
import com.example.service1.entities.customeraccount;
import com.example.service1.entities.productsellingcompany;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.GeographicCoverageService;
import com.example.service1.services.ProductSellingCompanyAccountService;
//import com.example.service1.services.ShippingCompanyService;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

//AMA BT3ML STATELESS BEYLOGIN MARA WAHDA BAS
@Path("/admin")
public class AdminController {

    @EJB
    private ProductSellingCompanyAccountService sellingCompanyService;

//    @EJB
//    private ShippingCompanyService shippingCompanyService;

    @EJB
    private CustomerAccountService customerAccountService;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(admin admin) {
        try {
            System.out.println("USERNAME : "+  admin.getUsername());
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
    @Path("/registerShippingCompany/{name}/{geographic_coverage}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerShippingCompany(@PathParam("name") String company_name, @PathParam("geographic_coverage") String geographic_coverage) {
        String baseUrl ="http://localhost:16957/Service2_ShippingCompany-1.0-SNAPSHOT/api/shipping-resource/";
        if(GeographicCoverageService.getInstance().isRegionSupported(geographic_coverage)) {
            try {
                String password = generateRandomPassword();
                System.out.println("CREATING SHIPPING COMPANY");
                System.out.println("COMPANY NAME: " + company_name);
                try {
                    String URL = baseUrl + "createShippingCompany" + "/" + company_name + "/" + password + "/" + geographic_coverage;
                    HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .header("Content-Type", "application/json")
                            .timeout(java.time.Duration.ofMinutes(1))
                            .POST(HttpRequest.BodyPublishers.ofString(""))
                            .build(), HttpResponse.BodyHandlers.ofString());

                    return Response.status(Response.Status.CREATED).entity("Registered Successfully").build();
                }catch(Exception e){
                    e.printStackTrace();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to send notification").build();
                }
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to register shipping company").build();
            }
        }else{
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Coverage Not Valid!").build();
        }
    }

    @GET
    @Path("/listShippingCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShippingCompanies() throws URISyntaxException {
        String baseUrl ="http://localhost:16957/Service2_ShippingCompany-1.0-SNAPSHOT/api/shipping-resource/";
        String URL = baseUrl + "listShippingCompanies";
        try
        {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(URL)).GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    @DELETE
//    @Path("/deleteShippingCompany/{id}")
//    public Response deleteShippingCompany(@PathParam("id") Long companyId) {
//        try {
//            shippingCompanyService.deleteShippingCompany(companyId);
//            return Response.status(Status.OK).build();
//        } catch (Exception e) {
//            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to delete shipping company").build();
//        }
//    }

    @GET
    @Path("/listCustomerAccounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccounts() {
        List<customeraccount> accounts = customerAccountService.getAllCustomerAccounts();
        return Response.ok().entity(accounts).build();
    }

    private String generateRandomPassword() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        int PASSWORD_LENGTH = 8;

        Random random = new SecureRandom();

        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

