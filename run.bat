@echo off
setlocal
if not exist out mkdir out
javac -encoding UTF-8 -d out src\com\smartqueue\Main.java src\com\smartqueue\SmartQueueApplication.java src\com\smartqueue\menu\Menu.java src\com\smartqueue\model\Admin.java src\com\smartqueue\model\Payment.java src\com\smartqueue\model\QueueToken.java src\com\smartqueue\model\Student.java src\com\smartqueue\repository\PaymentRepository.java src\com\smartqueue\repository\QueueRepository.java src\com\smartqueue\repository\StudentRepository.java src\com\smartqueue\service\AdminService.java src\com\smartqueue\service\QueueService.java src\com\smartqueue\service\StudentService.java src\com\smartqueue\util\FileManager.java src\com\smartqueue\util\PasswordInput.java src\com\smartqueue\util\Validation.java
if errorlevel 1 pause & exit /b 1
java -cp out com.smartqueue.Main
pause
