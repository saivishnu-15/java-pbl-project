package com.smartqueue.repository;

import com.smartqueue.model.Payment;
import com.smartqueue.util.FileManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private static final String FILE = "payments.txt";
    private final List<Payment> payments = new ArrayList<>();

    public PaymentRepository() { load(); }

    public void savePayment(Payment payment) { payments.add(payment); persist(); }
    public List<Payment> getAllPayments() { return new ArrayList<>(payments); }

    public List<Payment> getPaymentsByStudent(int studentId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) if (p.getStudentId() == studentId) result.add(p);
        return result;
    }

    public Payment findByReceipt(String receiptNumber) {
        for (Payment p : payments) if (p.getReceiptNumber().equalsIgnoreCase(receiptNumber)) return p;
        return null;
    }

    public double getDailyCollection(LocalDate date) {
        double total = 0;
        for (Payment p : payments) if (p.getPaymentDate().equals(date)) total += p.getAmountPaid();
        return total;
    }

    public int getDailyPaymentCount(LocalDate date) {
        int count = 0;
        for (Payment p : payments) if (p.getPaymentDate().equals(date)) count++;
        return count;
    }

    public int highestReceiptNumber() {
        int max = 1000;
        for (Payment p : payments) {
            try { max = Math.max(max, Integer.parseInt(p.getReceiptNumber().replaceAll("\\D", ""))); }
            catch (Exception ignored) { }
        }
        return max;
    }

    private void load() {
        for (String line : FileManager.readAll(FILE)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length != 8) continue;
                LocalDate due = p[7].equals("NONE") ? null : LocalDate.parse(p[7]);
                payments.add(new Payment(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                        Double.parseDouble(p[3]), Double.parseDouble(p[4]), LocalDate.parse(p[5]),
                        LocalTime.parse(p[6]), due));
            } catch (Exception ignored) { }
        }
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Payment p : payments) {
            lines.add(p.getReceiptNumber() + "|" + p.getStudentId() + "|" + p.getTokenNumber() + "|" +
                    p.getAmountPaid() + "|" + p.getBalanceAfterPayment() + "|" + p.getPaymentDate() + "|" +
                    p.getPaymentTime() + "|" + (p.getNextDueDate() == null ? "NONE" : p.getNextDueDate()));
        }
        FileManager.overwrite(FILE, lines);
    }
}
