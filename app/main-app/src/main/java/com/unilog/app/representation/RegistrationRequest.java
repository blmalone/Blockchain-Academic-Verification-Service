package com.unilog.app.representation;

public class RegistrationRequest {

    private String recipientEmailAddress;

    private String recipientAccountAddress;

    private String code;

    private String institutionEmailAddress;

    private String activationInstitutionEmailAddress;

    private String institutionCode;

    private String message;

    private boolean registryCreated;

    private String unilogRegistryContractAddress;

    private String newPassword;

    private String repeatedNewPassword;

    public String getRecipientEmailAddress() {
        return recipientEmailAddress;
    }

    public void setRecipientEmailAddress(final String recipientEmailAddress) {
        this.recipientEmailAddress = recipientEmailAddress;
    }

    public String getRecipientAccountAddress() {
        return recipientAccountAddress;
    }

    public void setRecipientAccountAddress(final String recipientAccountAddress) {
        this.recipientAccountAddress = recipientAccountAddress;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getInstitutionEmailAddress() {
        return institutionEmailAddress;
    }

    public void setInstitutionEmailAddress(final String institutionEmailAddress) {
        this.institutionEmailAddress = institutionEmailAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(final String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getActivationInstitutionEmailAddress() {
        return activationInstitutionEmailAddress;
    }

    public void setActivationInstitutionEmailAddress(final String activationInstitutionEmailAddress) {
        this.activationInstitutionEmailAddress = activationInstitutionEmailAddress;
    }

    public String getUnilogRegistryContractAddress() {
        return unilogRegistryContractAddress;
    }

    public void setUnilogRegistryContractAddress(final String unilogRegistryContractAddress) {
        this.unilogRegistryContractAddress = unilogRegistryContractAddress;
    }

    public boolean isRegistryCreated() {
        return registryCreated;
    }

    public void setRegistryCreated(final boolean registryCreated) {
        this.registryCreated = registryCreated;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatedNewPassword() {
        return repeatedNewPassword;
    }

    public void setRepeatedNewPassword(final String repeatedNewPassword) {
        this.repeatedNewPassword = repeatedNewPassword;
    }
}
