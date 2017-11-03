package com.unilog.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "Qualification")
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(targetEntity = Transcript.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "qualification")
    private Set<Transcript> transcripts;

    private String qualificationCode;

    private String qualificationTitle;

    private String accreditedInstitution;

    @JsonIgnore
    private String qualificationHash;

    private String issuer;

    public Qualification() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getQualificationTitle() {
        return qualificationTitle;
    }

    public void setQualificationTitle(final String qualificationTitle) {
        this.qualificationTitle = qualificationTitle;
    }

    public String getAccreditedInstitution() {
        return accreditedInstitution;
    }

    public void setAccreditedInstitution(final String accreditedInstitution) {
        this.accreditedInstitution = accreditedInstitution;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Set<Transcript> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(final Set<Transcript> transcripts) {
        this.transcripts = transcripts;
    }

    public String getQualificationHash() {
        return qualificationHash;
    }

    public void setQualificationHash(final String qualificationHash) {
        this.qualificationHash = qualificationHash;
    }

    public String getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(final String qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }
}
