package com.example.service1.services;

import com.example.service1.entities.orders;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Singleton
public class GeographicCoverageService {

    private List<String> supportedRegions = new ArrayList<>();

    private List<String> companyNames = new ArrayList<>();

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @PostConstruct
    public void init() {
        TypedQuery<String> query2 = entityManager.createQuery("SELECT o.company_name FROM shippingcompany o", String.class);
        companyNames = query2.getResultList();

        for (String companyName : companyNames) {
            TypedQuery<String> query = entityManager.createQuery("SELECT o.geographic_coverage FROM shippingcompany o WHERE o.company_name = :companyName", String.class);
            query.setParameter("companyName", companyName);
            String supportedRegion = query.getSingleResult();
            supportedRegions.add(supportedRegion);
        }


    }
    public boolean isRegionSupported(String region) {return supportedRegions.contains(region);}

    public void setShippingCompany(String location, Long orderID, String customerName){
        int index = supportedRegions.indexOf(location);
        String shippingCompanyName = companyNames.get(index);

        TypedQuery<orders> query = entityManager.createQuery(
                "UPDATE orders o SET o.shipping_company = :shippingCompany, o.status = :status WHERE o.id = :orderId", orders.class);

        query.setParameter("shippingCompany", shippingCompanyName);
        query.setParameter("status", "processing");
        query.setParameter("orderId", orderID);

        int updatedRows = query.executeUpdate();

        System.out.println("UPDATED ROWS: " + updatedRows);







    }

}



