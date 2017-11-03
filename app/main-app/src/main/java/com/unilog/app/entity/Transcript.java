package com.unilog.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Transcript")
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "qualification")
    private Qualification qualification;

    private String transcriptReference;

    private boolean pass;

    private String contentAddress;

    private String recipientEmailAddress;

    private double classification;

    private String qualificationCode;

    private String qualificationTitle;

    private String accreditedInstitution;

    private String additionalInformation;

    public Transcript() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(final boolean pass) {
        this.pass = pass;
    }

    public String getContentAddress() {
        return contentAddress;
    }

    public void setContentAddress(final String contentAddress) {
        this.contentAddress = contentAddress;
    }

    public String getRecipientEmailAddress() {
        return recipientEmailAddress;
    }

    public void setRecipientEmailAddress(final String recipientEmailAddress) {
        this.recipientEmailAddress = recipientEmailAddress;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(final Qualification qualification) {
        this.qualification = qualification;
    }

    public String getTranscriptReference() {
        return transcriptReference;
    }

    public void setTranscriptReference(final String transcriptReference) {
        this.transcriptReference = transcriptReference;
    }

    public double getClassification() {
        return classification;
    }

    public void setClassification(final double classification) {
        this.classification = classification;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(final String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(final String qualificationCode) {
        this.qualificationCode = qualificationCode;
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
}
