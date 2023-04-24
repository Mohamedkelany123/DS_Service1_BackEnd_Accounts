package com.example.service1.services;

import com.example.service1.entities.orders;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

@Stateless
public class PurchaseOrderService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public List<orders> getAllPurchaseOrders(String customerName) {
        return entityManager.createQuery("SELECT o FROM orders o WHERE o.customerName = :customerName", orders.class)
                .setParameter("customerName", customerName)
                .getResultList();
    }


    public void createPurchaseOrder(orders purchaseOrder) {
        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(purchaseOrder);
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

}
