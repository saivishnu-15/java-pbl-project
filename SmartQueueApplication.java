package com.smartqueue;

import com.smartqueue.menu.Menu;
import com.smartqueue.repository.PaymentRepository;
import com.smartqueue.repository.QueueRepository;
import com.smartqueue.repository.StudentRepository;
import com.smartqueue.service.AdminService;
import com.smartqueue.service.QueueService;
import com.smartqueue.service.StudentService;

public class SmartQueueApplication {
    public void start() {
        StudentRepository studentRepository = new StudentRepository();
        QueueRepository queueRepository = new QueueRepository(studentRepository);
        PaymentRepository paymentRepository = new PaymentRepository();

        StudentService studentService = new StudentService(studentRepository, queueRepository, paymentRepository);
        QueueService queueService = new QueueService(queueRepository);
        AdminService adminService = new AdminService();

        new Menu(studentService, queueService, adminService).start();
    }
}
