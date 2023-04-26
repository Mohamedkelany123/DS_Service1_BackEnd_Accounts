//package com.example.service1.services;
//
//import com.example.service1.services.GeographicCoverageService;
//import com.example.service1.services.ShippingNotificationService;
//import com.example.service1.entities.orders;
//
//
//import jakarta.ejb.ActivationConfigProperty;
//import jakarta.ejb.EJB;
//import jakarta.ejb.MessageDriven;
//import jakarta.jms.Message;
//import jakarta.jms.MessageListener;
//import jakarta.jms.TextMessage;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//
//
//@MessageDriven(
//        name = "ShippingRequestProcessorMDB",
//        activationConfig = {
//                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/ShippingRequestQueue"),
//                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
//        }
//)
//public class ShippingRequestProcessor implements MessageListener {
//
//    @EJB
//    private GeographicCoverageService coverageService;
//
//    @EJB
//    private ShippingNotificationService notificationService;
//
//    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
//    private final EntityManager entityManager = emf.createEntityManager();
//
//    @Override
//    public void onMessage(Message message) {
//        try {
//            if (message instanceof TextMessage) {
//                TextMessage textMessage = (TextMessage) message;
//                String orderJson = textMessage.getText();
//                orders order = new ObjectMapper().readValue(orderJson, orders.class);
//                String shippingCompany = order.getShipping_company();
//                if (coverageService.isRegionSupported(order.getLocation())) {
//                    coverageService.setShippingCompany(order.getLocation(), order.getId(), order.getCustomerName());
//                    // process the order and notify the customer
//                    notificationService.sendShippingNotification(order.getCustomerName());
//                } else {
//                    // log that the order was rejected due to not being in the coverage area
//                    System.out.println("Order " + order.getId() + " was rejected by " + shippingCompany + " due to being outside of its geographic coverage area.");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
