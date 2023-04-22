package com.example.service1.entities;

import jakarta.persistence.*;
@Entity
@Table(name = "shippingcompany")
public class shippingcompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "geographic_coverage", nullable = false)
    private String geographicCoverage;

    @Column(name = "username", nullable = false)
    private String username;

    public shippingcompany() {}

    public shippingcompany(String companyName,String username, String password, String geographicCoverage) {
        this.companyName = companyName;
        this.password = password;
        this.geographicCoverage = geographicCoverage;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGeographicCoverage() {
        return geographicCoverage;
    }

    public void setGeographicCoverage(String geographicCoverage) {
        this.geographicCoverage = geographicCoverage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

