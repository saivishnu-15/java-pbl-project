package com.smartqueue.model;

public class Student {
    private int studentId;
    private String studentName;
    private String department;
    private int year;
    private String username;
    private String passwordHash;
    private double totalFee;
    private double balanceFee;

    public Student(int studentId, String studentName, String department, int year,
                   String username, String passwordHash, double totalFee, double balanceFee) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.department = department;
        this.year = year;
        this.username = username;
        this.passwordHash = passwordHash;
        this.totalFee = totalFee;
        this.balanceFee = balanceFee;
    }

    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public double getTotalFee() { return totalFee; }
    public double getBalanceFee() { return balanceFee; }

    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year) { this.year = year; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }
    public void setBalanceFee(double balanceFee) { this.balanceFee = balanceFee; }

    @Override
    public String toString() {
        return "Student ID   : " + studentId +
                "\nStudent Name : " + studentName +
                "\nDepartment   : " + department +
                "\nYear         : " + year +
                "\nTotal Fee    : Rs." + String.format("%.2f", totalFee) +
                "\nBalance Fee  : Rs." + String.format("%.2f", balanceFee);
    }
}
