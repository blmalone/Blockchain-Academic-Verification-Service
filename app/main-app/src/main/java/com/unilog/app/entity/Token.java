package com.unilog.app.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "token")
    private User user1;

    public Token() {
    }

    private String contractAddress;

    private int artifactId;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(final String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final int artifactId) {
        this.artifactId = artifactId;
    }
}
