package com.unilog.app.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "wallet")
    private User user;

    private String publicKey;

    private String privateKey;

    private String walletAddress;

    private String contractAddress;

    public Wallet() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(final String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(final String contractAddress) {
        this.contractAddress = contractAddress;
    }
}
