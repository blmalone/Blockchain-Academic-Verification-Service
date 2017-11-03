package com.unilog.app.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "User")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String emailAddress;

    private String registrationCode;

    private String accountAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user")
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user1")
    private Token token;

    private boolean completedRegistration;

    private boolean publishedTranscript;

    public User() {
        super();
    }

    public User(final String emailAddress, final Wallet wallet, final String registrationCode) {
        this.emailAddress = emailAddress;
        this.wallet = wallet;
        this.registrationCode = registrationCode;
        this.completedRegistration = false;
        this.publishedTranscript = false;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(final Wallet wallet) {
        this.wallet = wallet;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(final String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(final String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(final Token token) {
        this.token = token;
    }

    public boolean isCompletedRegistration() {
        return completedRegistration;
    }

    public void setCompletedRegistration(final boolean completedRegistration) {
        this.completedRegistration = completedRegistration;
    }

    public boolean isPublishedTranscript() {
        return publishedTranscript;
    }

    public void setPublishedTranscript(final boolean publishedTranscript) {
        this.publishedTranscript = publishedTranscript;
    }
}
