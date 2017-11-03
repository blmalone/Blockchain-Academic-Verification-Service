package com.unilog.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Institution")
public class Institution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String emailAddress;

    private String registrationCode;

    private String password;

    private boolean completedRegistration;

    public Institution() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(final String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public boolean isCompletedRegistration() {
        return completedRegistration;
    }

    public void setCompletedRegistration(final boolean completedRegistration) {
        this.completedRegistration = completedRegistration;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
