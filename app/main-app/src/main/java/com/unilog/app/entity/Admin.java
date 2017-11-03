package com.unilog.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Admin")
public class Admin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String registryContractAddress;

    private boolean registryPublished;

    public Admin() {
        super();
    }

    public Admin(final String registryContractAddress) {
        this.registryContractAddress = registryContractAddress;
        this.registryPublished = false;
    }

    public String getRegistryContractAddress() {
        return registryContractAddress;
    }

    public void setRegistryContractAddress(final String registryContractAddress) {
        this.registryContractAddress = registryContractAddress;
    }

    public boolean isRegistryPublished() {
        return registryPublished;
    }

    public void setRegistryPublished(final boolean registryPublished) {
        this.registryPublished = registryPublished;
    }
}
