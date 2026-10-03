package com.smartqueue.service;

import com.smartqueue.model.QueueToken;
import com.smartqueue.model.Student;
import com.smartqueue.repository.QueueRepository;

public class QueueService {
    private final QueueRepository queueRepository;
    private int nextToken;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
        this.nextToken = queueRepository.highestTokenNumber() + 1;
    }

    public QueueToken joinFeeQueue(Student student) {
        if (student == null || student.getBalanceFee() <= 0) return null;
        if (queueRepository.hasActiveToken(student.getStudentId())) return null;
        QueueToken token = new QueueToken(nextToken++, student, "FEE PAYMENT");
        int position = queueRepository.getQueue().size() + 1;
        token.setTimeSlot(calculateTimeSlot(position));
        queueRepository.addToQueue(token); // ENQUEUE
        return token;
    }

    public String calculateTimeSlot(int position) {
        if (position <= 0) return "Not assigned";
        int group = (position - 1) / 10;
        int start = 9 * 60 + group * 30;
        int end = start + 30;
        return formatTime(start) + " - " + formatTime(end);
    }

    private String formatTime(int minutes) {
        int h24 = (minutes / 60) % 24, m = minutes % 60;
        String ampm = h24 >= 12 ? "PM" : "AM";
        int h = h24 % 12; if (h == 0) h = 12;
        return String.format("%02d:%02d %s", h, m, ampm);
    }

    public int findPosition(int tokenNumber) {
        int position = 1;
        for (QueueToken token : queueRepository.getQueue()) {
            if (token.getTokenNumber() == tokenNumber) return position;
            position++;
        }
        return -1;
    }

    public int waitingTime(int tokenNumber) {
        int position = findPosition(tokenNumber);
        return position < 0 ? -1 : (position - 1) * 5;
    }

    public QueueToken peekNextStudent() { return queueRepository.peekNextStudent(); }
    public QueueToken findActiveToken(Student student) { return student == null ? null : queueRepository.findActiveToken(student.getStudentId()); }
    public QueueToken leaveQueue(Student student) {
        QueueToken active = findActiveToken(student);
        if (active == null) return null;
        QueueToken cancelled = queueRepository.cancelToken(active.getTokenNumber());
        recalculateTimeSlots();
        return cancelled;
    }
    public void markCurrentServing(QueueToken token) {
        if (token != null && queueRepository.peekNextStudent() == token) {
            token.setStatus("SERVING");
            queueRepository.persistState();
        }
    }

    public QueueToken serveNextStudent() {
        QueueToken served = queueRepository.serveNextStudent(); // DEQUEUE
        recalculateTimeSlots();
        return served;
    }

    public void recalculateTimeSlots() {
        int position = 1;
        for (QueueToken t : queueRepository.getQueue()) {
            t.setTimeSlot(calculateTimeSlot(position++));
        }
        queueRepository.persistState();
    }
    public int getQueueSize() { return queueRepository.getQueue().size(); }
    public java.util.Queue<QueueToken> getLiveQueue() { return queueRepository.getQueue(); }
    public int getServedToday() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int count = 0;
        for (QueueToken t : queueRepository.getServedHistory())
            if (t.getCreatedAt().toLocalDate().equals(today)) count++;
        return count;
    }

    public void showQueue() {
        System.out.println("\n==================== LIVE FCFS QUEUE ====================");
        if (queueRepository.isQueueEmpty()) {
            System.out.println("Queue Status : EMPTY");
            System.out.println("No students are currently waiting.");
            System.out.println("==========================================================");
            return;
        }

        System.out.println("Queue Status : ACTIVE");
        System.out.println("FCFS Rule   : First Come, First Serve (FIFO)");
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-5s %-7s %-12s %-18s %-17s %-10s %-20s%n",
                "Pos", "Token", "Student ID", "Student", "Time Slot", "Status", "Joined");
        System.out.println("----------------------------------------------------------");

        int pos = 1;
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

        for (QueueToken t : queueRepository.getQueue()) {
            System.out.printf("%-5d Q%-6d %-12d %-18s %-17s %-10s %-20s%n",
                    pos++, t.getTokenNumber(), t.getStudent().getStudentId(),
                    t.getStudent().getStudentName(), t.getTimeSlot(),
                    t.getStatus(), t.getCreatedAt().format(formatter));
        }

        QueueToken next = queueRepository.peekNextStudent();
        System.out.println("----------------------------------------------------------");
        System.out.println("NEXT TO SERVE : Q" + next.getTokenNumber() +
                " | Student ID: " + next.getStudent().getStudentId() +
                " | " + next.getStudent().getStudentName());
        System.out.println("Queue Length  : " + queueRepository.getQueue().size());
        System.out.println("==========================================================");
    }
}
