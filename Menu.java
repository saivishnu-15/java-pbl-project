package com.smartqueue.menu;

import com.smartqueue.model.Payment;
import com.smartqueue.model.QueueToken;
import com.smartqueue.model.Student;
import com.smartqueue.service.AdminService;
import com.smartqueue.service.QueueService;
import com.smartqueue.service.StudentService;
import com.smartqueue.util.PasswordInput;
import com.smartqueue.util.Validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/** Console presentation layer. It behaves like a college terminal portal rather than a flat demo menu. */
public class Menu {
    private final StudentService studentService;
    private final QueueService queueService;
    private final AdminService adminService;
    private final Scanner scanner = new Scanner(System.in);
    private Student loggedInStudent;

    public Menu(StudentService studentService, QueueService queueService, AdminService adminService) {
        this.studentService = studentService;
        this.queueService = queueService;
        this.adminService = adminService;
    }

    public void start() {
        while (true) {
            banner("ABC COLLEGE | SMART FEE QUEUE MANAGEMENT");
            System.out.println("System Status : ONLINE");
            System.out.println("Date          : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            System.out.println("------------------------------------------------------------");
            System.out.println("[1] Student Portal     [2] Accounts Portal     [3] Exit");
            System.out.print("Select portal : ");
            switch (readInt()) {
                case 1 -> studentPortal();
                case 2 -> accountsPortal();
                case 3 -> { System.out.println("Session closed. Thank you."); return; }
                default -> System.out.println("Please select a valid portal.");
            }
        }
    }

    private void studentPortal() {
        while (true) {
            banner("STUDENT PORTAL");
            System.out.println("[1] Register new student account");
            System.out.println("[2] Login");
            System.out.println("[3] Back");
            System.out.print("Action : ");
            int choice = readInt();
            if (choice == 1) register();
            else if (choice == 2) { if (login()) studentDashboard(); }
            else if (choice == 3) return;
            else System.out.println("Invalid action.");
        }
    }

    private void register() {
        banner("STUDENT ACCOUNT REGISTRATION");
        int id = readIntField("Student ID");
        String name = readLineField("Student Name");
        String department = readLineField("Department");
        int year = readIntField("Year (1-5)");
        String username = readLineField("Username");
        String password = PasswordInput.readPassword("Create Password : ");
        double fee = readDoubleField("Total Fee");

        if (!Validation.isValidStudentId(id)) { error("Student ID must be positive."); return; }
        if (!Validation.isValidName(name)) { error("Name must contain letters and spaces only."); return; }
        if (!Validation.isValidDepartment(department)) { error("Invalid department."); return; }
        if (!Validation.isValidYear(year)) { error("Year must be between 1 and 5."); return; }
        if (!Validation.isValidUsername(username)) { error("Username must be 4-20 letters/numbers/underscore."); return; }
        if (!Validation.isStrongPassword(password)) { error("Password requires 8+ chars, uppercase, lowercase, number and special character."); return; }
        if (!Validation.isValidAmount(fee)) { error("Fee must be greater than zero."); return; }

        Student s = studentService.registerStudent(id, name, department, year, username, password, fee);
        if (s == null) error("Registration failed: Student ID or username already exists.");
        else success("Account created successfully. Student ID " + id + " is ready for login.");
    }

    private boolean login() {
        banner("STUDENT AUTHENTICATION");
        String username = readLineField("Username");
        String password = PasswordInput.readPassword("Password : ");
        loggedInStudent = studentService.login(username, password);
        if (loggedInStudent == null) { error("Invalid username or password."); return false; }
        success("Authentication successful. Welcome, " + loggedInStudent.getStudentName() + ".");
        return true;
    }

    private void studentDashboard() {
        while (loggedInStudent != null) {
            banner("STUDENT DASHBOARD | " + loggedInStudent.getStudentName());
            printStudentSummary(loggedInStudent);
            System.out.println("[1] Join Fee Queue / Generate Token");
            System.out.println("[2] View My Queue Status");
            System.out.println("[3] Leave Current Queue");
            System.out.println("[4] View Payment History");
            System.out.println("[5] Search My Receipt");
            System.out.println("[6] View Profile");
            System.out.println("[7] Logout");
            System.out.print("Action : ");
            switch (readInt()) {
                case 1 -> joinQueue();
                case 2 -> myQueueStatus();
                case 3 -> leaveQueue();
                case 4 -> paymentHistory(loggedInStudent.getStudentId());
                case 5 -> searchReceipt(true);
                case 6 -> printStudentProfile(loggedInStudent);
                case 7 -> { loggedInStudent = null; success("Student session closed."); }
                default -> System.out.println("Invalid action.");
            }
        }
    }

    private void joinQueue() {
        if (loggedInStudent.getBalanceFee() <= 0) { info("No pending fee. Student is fully paid."); return; }
        QueueToken active = queueService.findActiveToken(loggedInStudent);
        if (active != null) {
            error("Duplicate prevented. Active token Q" + active.getTokenNumber() + " already exists.");
            printToken(active, queueService.findPosition(active.getTokenNumber()), queueService.waitingTime(active.getTokenNumber()));
            return;
        }
        QueueToken token = queueService.joinFeeQueue(loggedInStudent);
        if (token == null) { error("Unable to generate a token."); return; }
        int position = queueService.findPosition(token.getTokenNumber());
        success("Queue token generated successfully.");
        printToken(token, position, queueService.waitingTime(token.getTokenNumber()));
        info("FCFS rule: this token was placed at the rear of the queue.");
    }

    private void myQueueStatus() {
        QueueToken token = queueService.findActiveToken(loggedInStudent);
        if (token == null) { info("No active queue token. You may join the queue for a pending fee payment."); return; }
        printToken(token, queueService.findPosition(token.getTokenNumber()), queueService.waitingTime(token.getTokenNumber()));
    }

    private void leaveQueue() {
        QueueToken cancelled = queueService.leaveQueue(loggedInStudent);
        if (cancelled == null) { info("No active token found."); return; }
        success("Token Q" + cancelled.getTokenNumber() + " cancelled.");
        info("If the student returns later, the same Student ID is retained but a NEW token will be generated at the rear of the FIFO queue.");
    }

    private void accountsPortal() {
        banner("ACCOUNTS / ORGANIZATION PORTAL");
        String username = readLineField("Accounts Username");
        String password = PasswordInput.readPassword("Accounts Password : ");
        if (!adminService.loginStaff(username, password)) { error("Invalid accounts credentials."); return; }
        success("Accounts staff authenticated.");

        while (adminService.isStaffLoggedIn()) {
            banner("ACCOUNTS DASHBOARD");
            System.out.println("Students Waiting : " + queueService.getQueueSize());
            System.out.println("Served Today    : " + queueService.getServedToday());
            System.out.printf("Today's Collection : Rs.%,.2f%n", studentService.getDailyCollection(LocalDate.now()));
            System.out.println("------------------------------------------------------------");
            queueService.showQueue();
            System.out.println("[1] Serve Next Student (FCFS)");
            System.out.println("[2] Process Current Payment");
            System.out.println("[3] Search Student by ID");
            System.out.println("[4] Search Receipt Number");
            System.out.println("[5] View Student Payment History");
            System.out.println("[6] Daily Collection Report");
            System.out.println("[7] Logout Accounts");
            System.out.print("Action : ");
            switch (readInt()) {
                case 1 -> serveNext();
                case 2 -> processCurrentPayment();
                case 3 -> searchStudent();
                case 4 -> searchReceipt(false);
                case 5 -> paymentHistory(readIntField("Student ID"));
                case 6 -> dailyReport();
                case 7 -> adminService.logoutStaff();
                default -> System.out.println("Invalid action.");
            }
        }
    }

    private void serveNext() {
        QueueToken next = queueService.peekNextStudent();
        if (next == null) { info("Queue is empty."); return; }
        if ("SERVING".equals(next.getStatus())) { info("Current turn is already being served. Process its payment."); return; }
        queueService.markCurrentServing(next);
        success("FCFS turn assigned.");
        printToken(next, 1, 0);
        info("The front student remains the current turn until a valid payment is completed.");
    }

    private void processCurrentPayment() {
        QueueToken current = queueService.peekNextStudent();
        if (current == null) { info("No student is waiting."); return; }
        if (!"SERVING".equals(current.getStatus())) {
            error("Select 'Serve Next Student' first. FCFS requires the front token to be served first."); return;
        }
        Student student = current.getStudent();
        System.out.printf("Student ID : %d | Name : %s | Pending Fee : Rs.%,.2f%n",
                student.getStudentId(), student.getStudentName(), student.getBalanceFee());
        double amount = readDoubleField("Payment Amount");
        if (!Validation.isValidAmount(amount) || amount > student.getBalanceFee()) {
            error("Invalid payment. Amount must be positive and cannot exceed the pending fee.");
            info("Student remains at the front of the queue.");
            return;
        }
        Payment payment = studentService.processPayment(student, current, amount);
        if (payment == null) { error("Payment could not be completed."); return; }
        queueService.serveNextStudent();
        success("Payment successful. FCFS token Q" + current.getTokenNumber() + " is now SERVED.");
        studentService.printReceipt(payment, student);
        if (payment.getBalanceAfterPayment() > 0)
            info("Next payment is due by " + payment.getNextDueDate() + ". The student can later rejoin with a NEW queue token.");
        else success("Fees are fully paid.");
    }

    private void searchStudent() {
        Student s = studentService.findStudent(readIntField("Student ID"));
        if (s == null) { error("Student not found."); return; }
        printStudentProfile(s);
        List<Payment> history = studentService.getPaymentHistory(s.getStudentId());
        System.out.println("Payment Count : " + history.size());
        QueueToken active = queueService.findActiveToken(s);
        System.out.println("Active Token  : " + (active == null ? "NONE" : "Q" + active.getTokenNumber()));
    }

    private void searchReceipt(boolean ownOnly) {
        String receipt = readLineField("Receipt Number");
        Payment p = studentService.findReceipt(receipt);
        if (p == null) { error("Receipt not found."); return; }
        if (ownOnly && p.getStudentId() != loggedInStudent.getStudentId()) { error("Receipt does not belong to the logged-in student."); return; }
        Student s = studentService.findStudent(p.getStudentId());
        if (s != null) studentService.printReceipt(p, s);
    }

    private void paymentHistory(int studentId) {
        List<Payment> history = studentService.getPaymentHistory(studentId);
        Student s = studentService.findStudent(studentId);
        if (history.isEmpty()) { info("No payment history found for Student ID " + studentId + "."); return; }
        System.out.println("\n================ PAYMENT HISTORY ================");
        System.out.println("Student ID : " + studentId + " | Name : " + (s == null ? "-" : s.getStudentName()));
        System.out.println("Receipt    Token    Amount          Balance       Date/Time");
        System.out.println("------------------------------------------------------------");
        for (Payment p : history) {
            System.out.printf("%-10s Q%-7d Rs.%-12.2f Rs.%-10.2f %s%n",
                    p.getReceiptNumber(), p.getTokenNumber(), p.getAmountPaid(), p.getBalanceAfterPayment(),
                    p.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + " " +
                            p.getPaymentTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        }
        if (s != null) System.out.printf("Total Fee : Rs.%,.2f | Current Balance : Rs.%,.2f%n", s.getTotalFee(), s.getBalanceFee());
    }

    private void dailyReport() {
        LocalDate today = LocalDate.now();
        System.out.println("\n================ DAILY COLLECTION REPORT ================");
        System.out.println("Date           : " + today.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        System.out.println("Payment Count  : " + studentService.getDailyPaymentCount(today));
        System.out.printf("Total Collection: Rs.%,.2f%n", studentService.getDailyCollection(today));
    }

    private void printStudentSummary(Student s) {
        double paid = s.getTotalFee() - s.getBalanceFee();
        System.out.printf("Student ID : %d | Department : %s | Year : %d%n", s.getStudentId(), s.getDepartment(), s.getYear());
        System.out.printf("Total Fee  : Rs.%,.2f | Paid : Rs.%,.2f | Balance : Rs.%,.2f%n", s.getTotalFee(), paid, s.getBalanceFee());
        QueueToken active = queueService.findActiveToken(s);
        System.out.println("Active Token : " + (active == null ? "NONE" : "Q" + active.getTokenNumber() + " | " + active.getTimeSlot()));
        System.out.println("------------------------------------------------------------");
    }

    private void printStudentProfile(Student s) {
        System.out.println("\n================ STUDENT PROFILE ================");
        System.out.println("Student ID : " + s.getStudentId());
        System.out.println("Name       : " + s.getStudentName());
        System.out.println("Department : " + s.getDepartment());
        System.out.println("Year       : " + s.getYear());
        System.out.println("Username   : " + s.getUsername());
        System.out.printf("Total Fee  : Rs.%,.2f%n", s.getTotalFee());
        System.out.printf("Paid So Far: Rs.%,.2f%n", s.getTotalFee() - s.getBalanceFee());
        System.out.printf("Balance    : Rs.%,.2f%n", s.getBalanceFee());
    }

    private void printToken(QueueToken t, int position, int wait) {
        System.out.println("\n================ QUEUE TOKEN ================");
        System.out.println("Student ID       : " + t.getStudent().getStudentId());
        System.out.println("Token Number     : Q" + t.getTokenNumber());
        System.out.println("Queue Position   : " + position);
        System.out.println("Estimated Wait   : " + Math.max(wait, 0) + " minutes");
        System.out.println("Time Slot        : " + t.getTimeSlot());
        System.out.println("Status           : " + t.getStatus());
        System.out.println("Queue Discipline : FCFS / FIFO");
        System.out.println("==============================================");
    }

    private String readLineField(String label) { System.out.print(label + " : "); return scanner.nextLine().trim(); }
    private int readIntField(String label) { System.out.print(label + " : "); return readInt(); }
    private double readDoubleField(String label) { System.out.print(label + " : Rs."); return readDouble(); }

    private int readInt() { while (true) { try { return Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) { System.out.print("Enter a valid number : "); } } }
    private double readDouble() { while (true) { try { return Double.parseDouble(scanner.nextLine().trim()); } catch (Exception e) { System.out.print("Enter a valid amount : Rs."); } } }

    private void banner(String title) {
        System.out.println("\n============================================================");
        System.out.println(" " + title);
        System.out.println("============================================================");
    }
    private void success(String message) { System.out.println("[SUCCESS] " + message); }
    private void error(String message) { System.out.println("[ERROR] " + message); }
    private void info(String message) { System.out.println("[INFO] " + message); }
}
