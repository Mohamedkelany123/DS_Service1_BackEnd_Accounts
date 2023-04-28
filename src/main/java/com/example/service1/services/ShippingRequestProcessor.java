package com.example.service1.services;

//import com.example.service1.services.GeographicCoverageService;
//import com.example.service1.services.ShippingNotificationService;
import com.example.service1.entities.orders;


import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


@MessageDriven( activationConfig = {
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ShippingRequestQueue") },
        mappedName = "java:/jms/queue/ShippingRequestQueue", name = "ShippingRequestProcessor")
public class ShippingRequestProcessor implements MessageListener {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @Override
    public void onMessage(Message message) {

        try
        {
            //PERSIST IN DATABASE
            String orderRequest = message.getBody(String.class);
            System.out.println("Received message: " + orderRequest);
        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }
}
