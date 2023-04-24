package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    //status: current status of the order (e.g. "pending", "processing", "shipped")
    @Column(name = "status")
    private String status;

    @Column(name = "products")
    private String products;

    @Column(name = "shipping_company")
    private String shipping_company;

    public orders(){}
    public orders(String customerName, String products) {
        this.customerName = customerName;
        this.products = products;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusProcessing() {
        this.status = "processing";
    }
    public void setStatusShipped() {this.status = "shipped";}

    public String getShipping_company() {return shipping_company;}

    public void setShipping_company(String shipping_company) {this.shipping_company = shipping_company;}
}

