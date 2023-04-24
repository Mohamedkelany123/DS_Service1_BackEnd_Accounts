package com.example.service1.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import java.util.ArrayList;
import java.util.List;


@Stateful
public class cart {

    private List<String> cartItems;

    @Inject
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            cartItems = (List<String>) session.getAttribute("cart");
        } else {
            cartItems = new ArrayList<>();
        }
    }

    public void addProduct(String name) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems = (List<String>) request.getSession().getAttribute("cart");
        cartItems.add(name);
        request.getSession().setAttribute("cart", cartItems);
    }

    public void removeProduct(String name) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems = (List<String>) request.getSession().getAttribute("cart");
        cartItems.remove(name);
        request.getSession().setAttribute("cart", cartItems);
    }

    public void clearCart() {
        cartItems = new ArrayList<>();
        request.getSession().setAttribute("cart", cartItems);
    }

    public List<String> listCartItems() {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems = (List<String>) request.getSession().getAttribute("cart");
        return cartItems;
    }

    @PreDestroy
    public void destroy() {
        // Perform any cleanup operations, if necessary
    }

}
