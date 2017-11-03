package com.unilog.app.representation;

import com.unilog.app.entity.User;

import java.util.List;

public class AdminWindow {

    private String accountBalance;

    private List<User> users;

    private boolean registryPublished;

    private String registryContractAddress;

    private String selectedID;

    private String newAccountEmail;

    public AdminWindow() {
        this.registryPublished = false;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(final String accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getSelectedID() {
        return selectedID;
    }

    public void setSelectedID(final String selectedID) {
        this.selectedID = selectedID;
    }

    public boolean isRegistryPublished() {
        return registryPublished;
    }

    public void setRegistryPublished(final boolean registryPublished) {
        this.registryPublished = registryPublished;
    }

    public String getRegistryContractAddress() {
        return registryContractAddress;
    }

    public void setRegistryContractAddress(final String registryContractAddress) {
        this.registryContractAddress = registryContractAddress;
    }

    public String getNewAccountEmail() {
        return newAccountEmail;
    }

    public void setNewAccountEmail(final String newAccountEmail) {
        this.newAccountEmail = newAccountEmail;
    }
}
