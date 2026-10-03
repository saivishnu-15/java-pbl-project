package com.smartqueue.util;

public class Validation {
    public static boolean isValidStudentId(int id) { return id > 0; }
    public static boolean isValidName(String value) { return value != null && value.trim().matches("[A-Za-z ]{2,50}"); }
    public static boolean isValidDepartment(String value) { return value != null && value.trim().matches("[A-Za-z ]{2,40}"); }
    public static boolean isValidYear(int year) { return year >= 1 && year <= 5; }
    public static boolean isValidUsername(String value) { return value != null && value.matches("[A-Za-z0-9_]{4,20}"); }
    public static boolean isStrongPassword(String value) {
        return value != null && value.length() >= 8
                && value.matches(".*[A-Z].*")
                && value.matches(".*[a-z].*")
                && value.matches(".*[0-9].*")
                && value.matches(".*[^A-Za-z0-9].*");
    }
    public static boolean isValidAmount(double amount) { return amount > 0 && Double.isFinite(amount); }
}
