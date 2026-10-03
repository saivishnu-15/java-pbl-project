package com.smartqueue.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    private String receiptNumber;
    private int studentId;
    private int tokenNumber;
    private double amountPaid;
    private double balanceAfterPayment;
    private LocalDate paymentDate;
    private LocalTime paymentTime;
    private LocalDate nextDueDate;

    public Payment(String receiptNumber, int studentId, int tokenNumber,
                   double amountPaid, double balanceAfterPayment,
                   LocalDate paymentDate, LocalTime paymentTime,
                   LocalDate nextDueDate) {
        this.receiptNumber = receiptNumber;
        this.studentId = studentId;
        this.tokenNumber = tokenNumber;
        this.amountPaid = amountPaid;
        this.balanceAfterPayment = balanceAfterPayment;
        this.paymentDate = paymentDate;
        this.paymentTime = paymentTime;
        this.nextDueDate = nextDueDate;
    }

    public String getReceiptNumber() { return receiptNumber; }
    public int getStudentId() { return studentId; }
    public int getTokenNumber() { return tokenNumber; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalanceAfterPayment() { return balanceAfterPayment; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public LocalTime getPaymentTime() { return paymentTime; }
    public LocalDate getNextDueDate() { return nextDueDate; }

    @Override
    public String toString() {
        String due = nextDueDate == null ? "FULLY PAID" : nextDueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return "Receipt: " + receiptNumber +
                " | Student: " + studentId +
                " | Token: Q" + tokenNumber +
                " | Paid: Rs." + String.format("%.2f", amountPaid) +
                " | Balance: Rs." + String.format("%.2f", balanceAfterPayment) +
                " | Date: " + paymentDate +
                " | Due: " + due;
    }
}
