package com.unilog.app.representation;

public class Token {

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
