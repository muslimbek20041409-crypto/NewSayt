package com.example.testsaytproyekt.telegrambot.service;

import com.example.testsaytproyekt.admin.entity.Admin;
import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TelegramCodeService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public String handleContact(Message message) {
        Contact contact = message.getContact();
        Long telegramId = message.getFrom().getId();

        if (contact.getUserId() == null || !contact.getUserId().equals(telegramId)) {
            return "Iltimos, aynan o‘zingizning telefon raqamingizni yuboring.";
        }

        String normalizedPhone = normalizePhoneNumber(contact.getPhoneNumber());

        Optional<Student> studentOpt = studentRepository.findByPhoneNumber(normalizedPhone);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setTelegramId(telegramId);
            ensureVerificationCode(student);
            studentRepository.save(student);
            return """
                    Student sifatida topildingiz.
                    Sizning verification codingiz: %s

                    Buyruqlar:
                    - Kodni tasdiqlash: 123456
                    - Kodni qayta yuborish: /resend
                    - Parol tiklash: /forgot
                    """.formatted(student.getVerificationCode());
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByPhoneNumber(normalizedPhone);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            teacher.setTelegramId(telegramId);
            ensureVerificationCode(teacher);
            teacherRepository.save(teacher);
            return """
                    Teacher sifatida topildingiz.
                    Sizning verification codingiz: %s

                    Buyruqlar:
                    - Kodni tasdiqlash: 123456
                    - Kodni qayta yuborish: /resend
                    - Parol tiklash: /forgot
                    """.formatted(teacher.getVerificationCode());
        }

        Optional<Admin> adminOpt = adminRepository.findByphoneNumber(normalizedPhone);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setTelegramId(telegramId);
            ensureVerificationCode(admin);
            adminRepository.save(admin);
            return """
                    Admin sifatida topildingiz.
                    Sizning verification codingiz: %s

                    Buyruqlar:
                    - Kodni tasdiqlash: 123456
                    - Kodni qayta yuborish: /resend
                    - Parol tiklash: /forgot
                    """.formatted(admin.getVerificationCode());
        }

        return "Bu raqam bilan hech qanday foydalanuvchi topilmadi.";
    }

    public String handleTextMessage(Long telegramId, String text) {
        if ("/resend".equalsIgnoreCase(text)) {
            return resendCode(telegramId);
        }

        if ("/forgot".equalsIgnoreCase(text)) {
            return sendPasswordResetCode(telegramId);
        }

        if (text.matches("\\d{4,8}")) {
            return verifyCode(telegramId, text);
        }

        if (text.startsWith("/newpass ")) {
            String[] parts = text.split("\\s+", 3);
            if (parts.length < 3) {
                return "Format: /newpass KOD YANGI_PAROL";
            }
            return resetPassword(telegramId, parts[1], parts[2]);
        }

        return """
                Noma’lum buyruq.
                Ishlatish mumkin:
                /start
                /resend
                /forgot
                /newpass KOD YANGI_PAROL
                yoki verification code yuboring.
                """;
    }

    public String resendCode(Long telegramId) {
        Optional<Student> studentOpt = studentRepository.findByTelegramId(telegramId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String code = generateCode();
            student.setVerificationCode(code);
            student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            studentRepository.save(student);
            return "Yangi verification code: " + code;
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByTelegramId(telegramId);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            String code = generateCode();
            teacher.setVerificationCode(code);
            teacher.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            teacherRepository.save(teacher);
            return "Yangi verification code: " + code;
        }

        Optional<Admin> adminOpt = adminRepository.findByTelegramId(telegramId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            String code = generateCode();
            admin.setVerificationCode(code);
            admin.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            adminRepository.save(admin);
            return "Yangi verification code: " + code;
        }

        return "Avval /start bosing va raqamingizni yuboring.";
    }

    public String verifyCode(Long telegramId, String code) {
        Optional<Student> studentOpt = studentRepository.findByTelegramId(telegramId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            return verifyStudentCode(student, code);
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByTelegramId(telegramId);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            return verifyTeacherCode(teacher, code);
        }

        Optional<Admin> adminOpt = adminRepository.findByTelegramId(telegramId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return verifyAdminCode(admin, code);
        }

        return "Avval /start bosing va raqamingizni yuboring.";
    }

    public String sendPasswordResetCode(Long telegramId) {
        Optional<Student> studentOpt = studentRepository.findByTelegramId(telegramId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String code = generateCode();
            student.setVerificationCode(code);
            student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            studentRepository.save(student);
            return "Parolni tiklash kodi: " + code;
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByTelegramId(telegramId);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            String code = generateCode();
            teacher.setVerificationCode(code);
            teacher.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            teacherRepository.save(teacher);
            return "Parolni tiklash kodi: " + code;
        }

        Optional<Admin> adminOpt = adminRepository.findByTelegramId(telegramId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            String code = generateCode();
            admin.setVerificationCode(code);
            admin.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
            adminRepository.save(admin);
            return "Parolni tiklash kodi: " + code;
        }

        return "Avval /start bosing va raqamingizni yuboring.";
    }

    public String resetPassword(Long telegramId, String code, String newPassword) {
        Optional<Student> studentOpt = studentRepository.findByTelegramId(telegramId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (!isCodeValid(student.getVerificationCode(), student.getCodeExpiryTime(), code)) {
                return "Kod noto‘g‘ri yoki muddati tugagan.";
            }
            student.setPassword(passwordEncoder.encode(newPassword));
            student.setVerificationCode(null);
            student.setCodeExpiryTime(null);
            studentRepository.save(student);
            return "Student paroli muvaffaqiyatli almashtirildi.";
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByTelegramId(telegramId);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            if (!isCodeValid(teacher.getVerificationCode(), teacher.getCodeExpiryTime(), code)) {
                return "Kod noto‘g‘ri yoki muddati tugagan.";
            }
            teacher.setPassword(passwordEncoder.encode(newPassword));
            teacher.setVerificationCode(null);
            teacher.setCodeExpiryTime(null);
            teacherRepository.save(teacher);
            return "Teacher paroli muvaffaqiyatli almashtirildi.";
        }

        Optional<Admin> adminOpt = adminRepository.findByTelegramId(telegramId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!isCodeValid(admin.getVerificationCode(), admin.getCodeExpiryTime(), code)) {
                return "Kod noto‘g‘ri yoki muddati tugagan.";
            }
            admin.setPassword(passwordEncoder.encode(newPassword));
            admin.setVerificationCode(null);
            admin.setCodeExpiryTime(null);
            adminRepository.save(admin);
            return "Admin paroli muvaffaqiyatli almashtirildi.";
        }

        return "Avval /start bosing va raqamingizni yuboring.";
    }

    private String verifyStudentCode(Student student, String code) {
        if (!isCodeValid(student.getVerificationCode(), student.getCodeExpiryTime(), code)) {
            return "Kod noto‘g‘ri yoki muddati tugagan.";
        }
        student.setVerified(true);
        student.setVerificationCode(null);
        student.setCodeExpiryTime(null);
        studentRepository.save(student);
        return "Student akkaunti muvaffaqiyatli tasdiqlandi.";
    }

    private String verifyTeacherCode(Teacher teacher, String code) {
        if (!isCodeValid(teacher.getVerificationCode(), teacher.getCodeExpiryTime(), code)) {
            return "Kod noto‘g‘ri yoki muddati tugagan.";
        }
        teacher.setVerified(true);
        teacher.setVerificationCode(null);
        teacher.setCodeExpiryTime(null);
        teacherRepository.save(teacher);
        return "Teacher akkaunti muvaffaqiyatli tasdiqlandi.";
    }

    private String verifyAdminCode(Admin admin, String code) {
        if (!isCodeValid(admin.getVerificationCode(), admin.getCodeExpiryTime(), code)) {
            return "Kod noto‘g‘ri yoki muddati tugagan.";
        }
        admin.setVerified(true);
        admin.setVerificationCode(null);
        admin.setCodeExpiryTime(null);
        adminRepository.save(admin);
        return "Admin akkaunti muvaffaqiyatli tasdiqlandi.";
    }

    private boolean isCodeValid(String savedCode, LocalDateTime expiryTime, String inputCode) {
        return savedCode != null
                && expiryTime != null
                && expiryTime.isAfter(LocalDateTime.now())
                && savedCode.equals(inputCode);
    }

    private void ensureVerificationCode(Student student) {
        if (student.getVerificationCode() == null || student.getCodeExpiryTime() == null
                || student.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            student.setVerificationCode(generateCode());
            student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private void ensureVerificationCode(Teacher teacher) {
        if (teacher.getVerificationCode() == null || teacher.getCodeExpiryTime() == null
                || teacher.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            teacher.setVerificationCode(generateCode());
            teacher.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private void ensureVerificationCode(Admin admin) {
        if (admin.getVerificationCode() == null || admin.getCodeExpiryTime() == null
                || admin.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            admin.setVerificationCode(generateCode());
            admin.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^\\d+]", "");
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }
        return cleaned;
    }
}