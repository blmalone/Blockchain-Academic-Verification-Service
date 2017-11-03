package com.unilog.app.representation;

public class Transcript {

    private long id;

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

    public String getTranscriptReference() {
        return transcriptReference;
    }

    public void setTranscriptReference(final String transcriptReference) {
        this.transcriptReference = transcriptReference;
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

    public double getClassification() {
        return classification;
    }

    public void setClassification(final double classification) {
        this.classification = classification;
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

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(final String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
