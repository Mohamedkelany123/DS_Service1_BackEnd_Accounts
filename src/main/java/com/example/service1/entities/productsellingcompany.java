package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "productsellingcompany")
public class productsellingcompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "password", nullable = false)
    private String password;


    public productsellingcompany() {}

    public productsellingcompany(String companyName, String password) {
        this.password = password;
        this.companyName = companyName;
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

}

