package com.smartqueue.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QueueToken {
    private int tokenNumber;
    private Student student;
    private String serviceType;
    private String status;
    private LocalDateTime createdAt;
    private String timeSlot;

    public QueueToken(int tokenNumber, Student student, String serviceType) {
        this(tokenNumber, student, serviceType, LocalDateTime.now(), "WAITING", "Not assigned");
    }

    private QueueToken(int tokenNumber, Student student, String serviceType,
                       LocalDateTime createdAt, String status, String timeSlot) {
        this.tokenNumber = tokenNumber;
        this.student = student;
        this.serviceType = serviceType;
        this.status = status;
        this.createdAt = createdAt;
        this.timeSlot = timeSlot;
    }

    public static QueueToken fromStored(int tokenNumber, Student student, String serviceType,
                                        LocalDateTime createdAt, String status, String timeSlot) {
        return new QueueToken(tokenNumber, student, serviceType, createdAt, status, timeSlot);
    }

    public int getTokenNumber() { return tokenNumber; }
    public Student getStudent() { return student; }
    public String getServiceType() { return serviceType; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
        return "---------------------------------------------\n" +
                "Token Number : Q" + tokenNumber + "\n" +
                "Student ID   : " + student.getStudentId() + "\n" +
                "Student Name : " + student.getStudentName() + "\n" +
                "Service      : " + serviceType + "\n" +
                "Status       : " + status + "\n" +
                "Joined At    : " + createdAt.format(f) + "\n" +
                "Time Slot    : " + timeSlot + "\n" +
                "---------------------------------------------";
    }
}
