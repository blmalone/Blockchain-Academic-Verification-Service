package com.unilog.app.representation;

import java.util.ArrayList;
import java.util.List;

public class Qualification {

    private long id;

    private List<Transcript> transcripts;

    private String qualificationCode;

    private String qualificationTitle;

    private String accreditedInstitution;

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

    public List<Transcript> getTranscripts() {
        if (transcripts == null) {
            transcripts = new ArrayList<Transcript>();
        }
        return transcripts;
    }

    public void setTranscripts(final List<Transcript> transcripts) {
        this.transcripts = transcripts;
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

    public String getQualificationHash() {
        return qualificationHash;
    }

    public void setQualificationHash(final String qualificationHash) {
        this.qualificationHash = qualificationHash;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }
}
