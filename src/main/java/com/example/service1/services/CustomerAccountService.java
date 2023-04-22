package com.example.service1.services;

import com.example.service1.entities.customeraccount;
import com.example.service1.entities.productsellingcompany;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Stateless
public class CustomerAccountService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public void register(String name, String username, String password) {
        customeraccount customer = new customeraccount(name, username, password);

        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(customer);
            entityManager.getTransaction().commit();
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
    }

    public boolean login(String username, String password) {
        TypedQuery<customeraccount> query = entityManager.createQuery("SELECT c FROM customeraccount c WHERE c.username = :username AND c.password = :password", customeraccount.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getResultList().size() == 1;
    }

    public List<customeraccount> getAllCustomerAccounts() {
        TypedQuery<customeraccount> query = entityManager.createQuery("SELECT c FROM customeraccount c", customeraccount.class);
        return query.getResultList();
    }
}
