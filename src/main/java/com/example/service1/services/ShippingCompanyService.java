package com.example.service1.services;



import com.example.service1.entities.shippingcompany;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;


@Stateless
public class ShippingCompanyService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public void createShippingCompany(String companyName,String geographicCoverage) {
        System.out.println("Creating password");
        String password = generateRandomPassword();
        System.out.println("BEFORE CONSTRUCTOR");
        shippingcompany company = new shippingcompany(companyName, password, geographicCoverage);
        System.out.println("AFTER CONSTRUCTOR");

        EntityManager entityManager = null;

        try {
            System.out.println("INSIDE TRY");
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(company);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("INSIDE CATCH");
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            System.out.println("INSIDE FINALLY");
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public List<shippingcompany> listShippingCompanies() {
        TypedQuery<shippingcompany> query = entityManager.createQuery("SELECT c FROM shippingcompany c", shippingcompany.class);
        return query.getResultList();
    }


    public void deleteShippingCompany(Long companyId) {
        shippingcompany company = entityManager.find(shippingcompany.class, companyId);
        if (company != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(company);
            entityManager.getTransaction().commit();
        }
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