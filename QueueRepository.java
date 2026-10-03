package com.smartqueue.repository;

import com.smartqueue.model.QueueToken;
import com.smartqueue.model.Student;
import com.smartqueue.util.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Repository for the live FIFO queue and queue history. */
public class QueueRepository {
    private static final String FILE = "queue.txt";
    private final Queue<QueueToken> queue = new LinkedList<>();
    private final List<QueueToken> servedHistory = new ArrayList<>();
    private final List<QueueToken> cancelledHistory = new ArrayList<>();

    public QueueRepository(StudentRepository students) { load(students); }

    public void addToQueue(QueueToken token) {
        queue.offer(token); // ENQUEUE at the rear
        persist();
    }

    public Queue<QueueToken> getQueue() { return new LinkedList<>(queue); }
    public boolean isQueueEmpty() { return queue.isEmpty(); }
    public QueueToken peekNextStudent() { return queue.peek(); }

    /** DEQUEUE from the front. */
    public void persistState() { persist(); }

    public QueueToken serveNextStudent() {
        QueueToken token = queue.poll();
        if (token != null) {
            token.setStatus("SERVED");
            servedHistory.add(token);
            persist();
        }
        return token;
    }

    public List<QueueToken> getServedHistory() { return new ArrayList<>(servedHistory); }
    public List<QueueToken> getCancelledHistory() { return new ArrayList<>(cancelledHistory); }

    public boolean hasActiveToken(int studentId) {
        return findActiveToken(studentId) != null;
    }

    public QueueToken findActiveToken(int studentId) {
        for (QueueToken t : queue) {
            if (t.getStudent().getStudentId() == studentId &&
                    ("WAITING".equals(t.getStatus()) || "SERVING".equals(t.getStatus()))) return t;
        }
        return null;
    }

    public QueueToken findByToken(int tokenNumber) {
        for (QueueToken t : queue) if (t.getTokenNumber() == tokenNumber) return t;
        return null;
    }

    public int highestTokenNumber() {
        int max = 0;
        for (QueueToken t : queue) max = Math.max(max, t.getTokenNumber());
        for (QueueToken t : servedHistory) max = Math.max(max, t.getTokenNumber());
        for (QueueToken t : cancelledHistory) max = Math.max(max, t.getTokenNumber());
        return max;
    }

    public QueueToken cancelToken(int tokenNumber) {
        QueueToken found = findByToken(tokenNumber);
        if (found != null) {
            queue.remove(found);
            found.setStatus("CANCELLED");
            cancelledHistory.add(found);
            persist();
        }
        return found;
    }

    private void load(StudentRepository students) {
        for (String line : FileManager.readAll(FILE)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length != 6) continue;
                int token = Integer.parseInt(p[0]);
                int studentId = Integer.parseInt(p[1]);
                Student student = students.findStudent(studentId);
                if (student == null) continue;
                QueueToken t = QueueToken.fromStored(token, student, p[2],
                        LocalDateTime.parse(p[3]), p[4], p[5]);
                if ("WAITING".equals(p[4]) || "SERVING".equals(p[4])) queue.offer(t);
                else if ("SERVED".equals(p[4])) servedHistory.add(t);
                else if ("CANCELLED".equals(p[4])) cancelledHistory.add(t);
            } catch (Exception ignored) { }
        }
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (QueueToken t : queue) lines.add(serialize(t));
        for (QueueToken t : servedHistory) lines.add(serialize(t));
        for (QueueToken t : cancelledHistory) lines.add(serialize(t));
        FileManager.overwrite(FILE, lines);
    }

    private String serialize(QueueToken t) {
        return t.getTokenNumber() + "|" + t.getStudent().getStudentId() + "|" +
                t.getServiceType() + "|" + t.getCreatedAt() + "|" +
                t.getStatus() + "|" + t.getTimeSlot();
    }
}
