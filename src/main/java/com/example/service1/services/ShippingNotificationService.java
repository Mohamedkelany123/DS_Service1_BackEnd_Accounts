//package com.example.service1.services;
//
//import com.example.service1.entities.orders;
//import jakarta.ejb.ActivationConfigProperty;
//import jakarta.ejb.EJB;
//import jakarta.ejb.MessageDriven;
//import jakarta.jms.Message;
//import jakarta.jms.MessageListener;
//import jakarta.jms.ObjectMessage;
//import jakarta.jms.TextMessage;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//
//import java.io.Serializable;
//
//@MessageDriven(
//        mappedName = "jms/shippingQueue",
//        activationConfig = {
//                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
//        }
//)
//public class ShippingNotificationService implements MessageListener {
//
//    @EJB
//    private CustomerAccountService customerAccountService;
//
//    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
//    private final EntityManager entityManager = emf.createEntityManager();
//
//    @Override
//    public void onMessage(Message message) {
//        try {
//            if (message instanceof ObjectMessage) {
//                ObjectMessage objectMessage = (ObjectMessage) message;
//                orders order = (orders) objectMessage.getObject();
//                String customerName = order.getCustomerName();
//                String shippingCompany = order.getShipping_company();
//                String region = order.getLocation();
//
//                GeographicCoverageService coverageService = new GeographicCoverageService();
//                if (coverageService.isRegionSupported(region)) {
//                    // Process shipping request and send notification to customer
//                } else {
//                    // Log error or send notification to shipping company
//                }
//            }
//        } catch (Exception e) {
//            // Handle exception
//        }
//    }
//
//}
