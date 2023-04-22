package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "shippingcompany")
public class shippingcompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "password")
    private String password;

    @Column(name = "geographic_coverage", columnDefinition = "text")
    private String geographicCoverage;

    public shippingcompany() {}

    public shippingcompany(String companyName, String password, String geographicCoverage) {
        this.companyName = companyName;
        this.password = password;
        this.geographicCoverage = geographicCoverage;
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
}
