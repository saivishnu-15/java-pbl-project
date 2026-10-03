package com.smartqueue.service;

import com.smartqueue.model.Admin;

public class AdminService {
    private final Admin accountsStaff = new Admin();
    private boolean staffLoggedIn = false;

    public boolean loginStaff(String username, String password) {
        staffLoggedIn = accountsStaff.getUsername().equals(username)
                && accountsStaff.getPassword().equals(password);
        return staffLoggedIn;
    }

    public boolean isStaffLoggedIn() { return staffLoggedIn; }
    public void logoutStaff() { staffLoggedIn = false; }
}
