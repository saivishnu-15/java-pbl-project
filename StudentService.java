package com.smartqueue.service;

import com.smartqueue.model.Payment;
import com.smartqueue.model.QueueToken;
import com.smartqueue.model.Student;
import com.smartqueue.repository.PaymentRepository;
import com.smartqueue.repository.QueueRepository;
import com.smartqueue.repository.StudentRepository;
import com.smartqueue.util.Validation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentService {
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final QueueRepository queueRepository;
    private int nextReceiptNumber;

    public StudentService(StudentRepository studentRepository, QueueRepository queueRepository, PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.queueRepository = queueRepository;
        this.paymentRepository = paymentRepository;
        this.nextReceiptNumber = paymentRepository.highestReceiptNumber() + 1;
    }

    public Student registerStudent(int id, String name, String department, int year, String username, String password, double totalFee) {
        if (!Validation.isValidStudentId(id) || !Validation.isValidName(name) || !Validation.isValidDepartment(department)
                || !Validation.isValidYear(year) || !Validation.isValidUsername(username)
                || !Validation.isStrongPassword(password) || !Validation.isValidAmount(totalFee)) return null;
        if (studentRepository.findStudent(id) != null || studentRepository.usernameExists(username)) return null;
        Student s = new Student(id, name.trim(), department.trim(), year, username, hashPassword(password), totalFee, totalFee);
        studentRepository.addStudent(s);
        return s;
    }

    public Student login(String username, String password) {
        Student s = studentRepository.findByUsername(username);
        return s != null && s.getPasswordHash().equals(hashPassword(password)) ? s : null;
    }

    public Student findStudent(int id) { return studentRepository.findStudent(id); }
    public List<Student> getAllStudents() { return studentRepository.getStudents(); }
    public void updateStudent(Student s) { studentRepository.updateStudent(s); }

    public Payment processPayment(Student student, QueueToken token, double amount) {
        if (student == null || token == null || !Validation.isValidAmount(amount) || amount > student.getBalanceFee()) return null;
        double newBalance = Math.max(0, student.getBalanceFee() - amount);
        student.setBalanceFee(newBalance);
        studentRepository.updateStudent(student);
        LocalDate date = LocalDate.now();
        LocalDate dueDate = newBalance > 0 ? date.plusDays(30) : null;
        Payment payment = new Payment("R" + nextReceiptNumber++, student.getStudentId(), token.getTokenNumber(),
                amount, newBalance, date, LocalTime.now(), dueDate);
        paymentRepository.savePayment(payment);
        return payment;
    }

    public List<Payment> getPaymentHistory(int studentId) { return paymentRepository.getPaymentsByStudent(studentId); }
    public Payment findReceipt(String receiptNumber) { return paymentRepository.findByReceipt(receiptNumber); }
    public double getDailyCollection(LocalDate date) { return paymentRepository.getDailyCollection(date); }
    public int getDailyPaymentCount(LocalDate date) { return paymentRepository.getDailyPaymentCount(date); }

    public void printReceipt(Payment payment, Student student) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm a");
        System.out.println("\n==================================================");
        System.out.println("                 PAYMENT RECEIPT");
        System.out.println("==================================================");
        System.out.println("Receipt Number : " + payment.getReceiptNumber());
        System.out.println("Student ID     : " + student.getStudentId());
        System.out.println("Student Name   : " + student.getStudentName());
        System.out.println("Department     : " + student.getDepartment());
        System.out.println("Token ID       : Q" + payment.getTokenNumber());
        System.out.printf("Total Fee      : Rs.%,.2f%n", student.getTotalFee());
        System.out.printf("Amount Paid    : Rs.%,.2f%n", payment.getAmountPaid());
        System.out.printf("Balance        : Rs.%,.2f%n", payment.getBalanceAfterPayment());
        System.out.println("Payment Date   : " + payment.getPaymentDate().format(date));
        System.out.println("Payment Time   : " + payment.getPaymentTime().format(time));
        System.out.println("Next Due Date  : " + (payment.getNextDueDate() == null ? "FULLY PAID" : payment.getNextDueDate().format(date)));
        System.out.println("==================================================");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new IllegalStateException("Password hashing unavailable", e); }
    }
}
