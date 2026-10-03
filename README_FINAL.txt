SMART QUEUE MANAGEMENT SYSTEM - FINAL CONSOLE PROJECT

Technology: Core Java, layered/package architecture, console terminal UI, Queue<QueueToken> using LinkedList for FCFS/FIFO, file-based repositories, SHA-256 password hashing.

TEACHER-REQUESTED FEATURES
1. Student registration with username/password
2. Password validation and hidden password input in a real system terminal
3. Student login/authentication
4. Student ID and student profile
5. Total fee, paid amount and pending balance
6. Fee-payment queue
7. Unique queue token generation
8. FCFS/FIFO using enqueue/dequeue
9. Queue position
10. Estimated waiting time
11. Time-slot allocation (10 positions per 30-minute slot from 09:00 AM)
12. Duplicate active-token prevention
13. Leave/cancel queue
14. Rejoin later with a NEW token while keeping the SAME Student ID
15. Accounts staff portal
16. Serve next student only in FCFS order
17. Payment processing and validation
18. Partial payment and remaining balance
19. Receipt generation
20. Receipt number + Student ID + Token ID correlation
21. Payment date and time
22. Student payment history
23. Search student by Student ID
24. Search receipt by receipt number
25. Daily collection report
26. Next payment/due-date information
27. Persistent student, queue and payment records

ARCHITECTURE
Console UI (Menu) -> Service Layer -> Repository Layer -> Model Layer -> File Storage

KEY CONCEPTS TO EXPLAIN
- Enqueue: QueueRepository.addToQueue() uses Queue.offer() to add a student at the rear.
- Dequeue: QueueRepository.serveNextStudent() uses Queue.poll() to remove the front student.
- FCFS: the front token is always served first.
- Duplicate prevention: QueueRepository.findActiveToken() checks whether the Student ID already has WAITING/SERVING token.
- Time slot: QueueService.calculateTimeSlot() groups every 10 positions into a 30-minute window.
- Partial payment: Student balance is reduced without changing Student ID.
- Second payment: after the old token is completed, the same Student ID can join again and receives a new token and receipt.
- Receipt retrieval: PaymentRepository.findByReceipt().
- Student retrieval: StudentRepository.findStudent().
- History: PaymentRepository.getPaymentsByStudent().
- Daily report: PaymentRepository.getDailyCollection() and getDailyPaymentCount().

DEMO ACCOUNTS
Accounts username: accounts
Accounts password: Accounts@2026

PASSWORD
Student passwords are stored as SHA-256 hashes. PasswordInput uses Console.readPassword() when launched from Command Prompt/PowerShell, so the password is not echoed. IntelliJ's built-in Run console does not provide java.io.Console; use a system terminal for a fully hidden password demonstration.

PERSISTENCE
Data is stored in the project's data/ directory at runtime:
students.txt
payments.txt
queue.txt
queue_history.txt (legacy/cancel logging)

The queue repository restores active and historical tokens and the token number continues after restart. Payment receipt numbering also continues after restart.


LIVE FCFS QUEUE DISPLAY UPDATE
The Accounts Dashboard automatically displays a formatted Live FCFS Queue before the action choices. It shows position, token, Student ID, student name, time slot, status, joined date/time, queue length and the next student to serve. Option [1] then performs the FCFS service operation.
