package com.smartqueue.model;

public class Admin {
    private final String username;
    private final String password;

    public Admin() {
        // Demo credentials for the accounts counter. Change them for deployment.
        this.username = "accounts";
        this.password = "Accounts@2026";
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
