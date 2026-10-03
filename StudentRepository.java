package com.smartqueue.repository;

import com.smartqueue.model.Student;
import com.smartqueue.util.FileManager;

import java.util.ArrayList;
import java.util.List;

public class StudentRepository {
    private final List<Student> students = new ArrayList<>();
    private static final String FILE = "students.txt";

    public StudentRepository() { load(); }

    public void addStudent(Student student) {
        students.add(student);
        persist();
    }

    public List<Student> getStudents() { return new ArrayList<>(students); }

    public Student findStudent(int studentId) {
        for (Student student : students) if (student.getStudentId() == studentId) return student;
        return null;
    }

    public Student findByUsername(String username) {
        for (Student student : students) if (student.getUsername().equalsIgnoreCase(username)) return student;
        return null;
    }

    public boolean usernameExists(String username) { return findByUsername(username) != null; }

    public void updateStudent(Student student) { persist(); }

    private void load() {
        for (String line : FileManager.readAll(FILE)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length != 8) continue;
                students.add(new Student(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]),
                        p[4], p[5], Double.parseDouble(p[6]), Double.parseDouble(p[7])));
            } catch (Exception ignored) { }
        }
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Student s : students) {
            lines.add(s.getStudentId() + "|" + s.getStudentName() + "|" + s.getDepartment() + "|" +
                    s.getYear() + "|" + s.getUsername() + "|" + s.getPasswordHash() + "|" +
                    s.getTotalFee() + "|" + s.getBalanceFee());
        }
        FileManager.overwrite(FILE, lines);
    }
}
