package com.example.testsaytproyekt.users.service;

import com.example.testsaytproyekt.admin.entity.Admin;
import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.enums.Role;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import com.example.testsaytproyekt.telegrambot.bot.TelegramBots;
import com.example.testsaytproyekt.telegrambot.dto.TelegramVerifyDto;
import com.example.testsaytproyekt.users.dto.*;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository repository;
    private final TelegramBots telegramBots;
    private final ModelMapper mapper = new ModelMapper();

    public List<StudentResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(student -> mapper.map(student, StudentResponseDto.class))
                .toList();
    }

    @Transactional
    public StudentResponseDto signUp(StudentCreateDto dto) {

        if (repository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username band");
        }

        String phone = normalizePhoneNumber(dto.getPhoneNumber());

        if (repository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("Bu telefon raqam bilan allaqachon ro‘yxatdan o‘tilgan");
        }

        Student student = Student.builder()
                .fullName(dto.getFullName())
                .phoneNumber(phone)
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.STUDENT)
                .active(true)
                .verified(false)
                .resendCount(0)
                .build();

        String code = generateCode();
        student.setVerificationCode(code);
        student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));

        repository.save(student);

        return mapper.map(student, StudentResponseDto.class);
    }

    @Transactional
    public AuthResponseDto signIn(StudentSignInDto dto) {

        Student student = repository.findByUsername(dto.getUsername()).orElse(null);
        if (student != null) {
            validatePasswordAndStatus(
                    student.getPassword(),
                    dto.getPassword(),
                    student.isVerified(),
                    "Student tasdiqlanmagan yoki parol noto‘g‘ri"
            );

            return AuthResponseDto.builder()
                    .id(student.getId())
                    .telegramId(student.getTelegramId())
                    .role(student.getRole())
                    .username(student.getUsername())
                    .fullName(student.getFullName())
                    .build();
        }

        Admin admin = adminRepository.findByUsername(dto.getUsername());
        if (admin != null) {
            validatePasswordAndStatus(
                    admin.getPassword(),
                    dto.getPassword(),
                    admin.isVerified(),
                    "Admin tasdiqlanmagan yoki parol noto‘g‘ri"
            );

            return AuthResponseDto.builder()
                    .id(admin.getId())
                    .telegramId(admin.getTelegramId())
                    .role(admin.getRole())
                    .username(admin.getUsername())
                    .fullName(admin.getFullName())
                    .build();
        }

        Teacher teacher = teacherRepository.findByUsername(dto.getUsername());
        if (teacher != null) {
            validatePasswordAndStatus(
                    teacher.getPassword(),
                    dto.getPassword(),
                    teacher.isVerified(),
                    "Teacher tasdiqlanmagan yoki parol noto‘g‘ri"
            );

            return AuthResponseDto.builder()
                    .id(teacher.getId())
                    .telegramId(teacher.getTelegramId())
                    .role(teacher.getRole())
                    .username(teacher.getUsername())
                    .fullName(teacher.getFullName())
                    .build();
        }

        throw new BadCredentialsException("Foydalanuvchi topilmadi");
    }

    @Transactional
    public void forgotPassword(ForgotPasswordDto dto) {
        String phone = normalizePhoneNumber(dto.getPhoneNumber());

        Student student = repository.findByPhoneNumber(phone).orElse(null);
        if (student != null) {
            sendResetCodeToStudent(student);
            return;
        }

        Admin admin = adminRepository.findByphoneNumber(phone).orElse(null);
        if (admin != null) {
            sendResetCodeToAdmin(admin);
            return;
        }

        Teacher teacher = teacherRepository.findByPhoneNumber(phone).orElse(null);
        if (teacher != null) {
            sendResetCodeToTeacher(teacher);
            return;
        }

        throw new RuntimeException("Bu telefon raqam bilan user topilmadi");
    }

    @Transactional
    public void verifyResetCode(VerifyResetCodeDto dto) {
        String phone = normalizePhoneNumber(dto.getPhoneNumber());
        String code = dto.getCode();

        Student student = repository.findByPhoneNumber(phone).orElse(null);
        if (student != null) {
            checkResetCode(student.getResetCode(), student.getResetCodeExpiryTime(), code);

            student.setResetVerified(true);
            repository.save(student);
            return;
        }

        Admin admin = adminRepository.findByphoneNumber(phone).orElse(null);
        if (admin != null) {
            checkResetCode(admin.getResetCode(), admin.getResetCodeExpiryTime(), code);

            admin.setResetVerified(true);
            adminRepository.save(admin);
            return;
        }

        Teacher teacher = teacherRepository.findByPhoneNumber(phone).orElse(null);
        if (teacher != null) {
            checkResetCode(teacher.getResetCode(), teacher.getResetCodeExpiryTime(), code);

            teacher.setResetVerified(true);
            teacherRepository.save(teacher);
            return;
        }

        throw new RuntimeException("Bu telefon raqam bilan user topilmadi");
    }

    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        String phone = normalizePhoneNumber(dto.getPhoneNumber());

        Student student = repository.findByPhoneNumber(phone).orElse(null);
        if (student != null) {
            resetStudentPassword(student, dto.getNewPassword());
            return;
        }

        Admin admin = adminRepository.findByphoneNumber(phone).orElse(null);
        if (admin != null) {
            resetAdminPassword(admin, dto.getNewPassword());
            return;
        }

        Teacher teacher = teacherRepository.findByPhoneNumber(phone).orElse(null);
        if (teacher != null) {
            resetTeacherPassword(teacher, dto.getNewPassword());
            return;
        }

        throw new RuntimeException("Bu telefon raqam bilan user topilmadi");
    }

    private void sendResetCodeToStudent(Student student) {
        if (student.getTelegramId() == null) {
            throw new RuntimeException("Bu akkaunt Telegram botga ulanmagan");
        }

        String code = generateCode();

        student.setResetCode(code);
        student.setResetCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        student.setResetVerified(false);

        repository.save(student);

        telegramBots.sendMessageToChat(
                student.getTelegramId(),
                "🔐 Parolni tiklash kodi: " + code + "\nKod 5 daqiqa amal qiladi."
        );
    }

    private void sendResetCodeToAdmin(Admin admin) {
        if (admin.getTelegramId() == null) {
            throw new RuntimeException("Bu akkaunt Telegram botga ulanmagan");
        }

        String code = generateCode();

        admin.setResetCode(code);
        admin.setResetCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        admin.setResetVerified(false);

        adminRepository.save(admin);

        telegramBots.sendMessageToChat(
                admin.getTelegramId(),
                "🔐 Parolni tiklash kodi: " + code + "\nKod 5 daqiqa amal qiladi."
        );
    }

    private void sendResetCodeToTeacher(Teacher teacher) {
        if (teacher.getTelegramId() == null) {
            throw new RuntimeException("Bu akkaunt Telegram botga ulanmagan");
        }

        String code = generateCode();

        teacher.setResetCode(code);
        teacher.setResetCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        teacher.setResetVerified(false);

        teacherRepository.save(teacher);

        telegramBots.sendMessageToChat(
                teacher.getTelegramId(),
                "🔐 Parolni tiklash kodi: " + code + "\nKod 5 daqiqa amal qiladi."
        );
    }

    private void resetStudentPassword(Student student, String newPassword) {
        checkResetVerified(student.getResetVerified(), student.getResetCodeExpiryTime());

        student.setPassword(passwordEncoder.encode(newPassword));
        student.setResetCode(null);
        student.setResetCodeExpiryTime(null);
        student.setResetVerified(false);

        repository.save(student);
    }

    private void resetAdminPassword(Admin admin, String newPassword) {
        checkResetVerified(admin.getResetVerified(), admin.getResetCodeExpiryTime());

        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setResetCode(null);
        admin.setResetCodeExpiryTime(null);
        admin.setResetVerified(false);

        adminRepository.save(admin);
    }

    private void resetTeacherPassword(Teacher teacher, String newPassword) {
        checkResetVerified(teacher.getResetVerified(), teacher.getResetCodeExpiryTime());

        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacher.setResetCode(null);
        teacher.setResetCodeExpiryTime(null);
        teacher.setResetVerified(false);

        teacherRepository.save(teacher);
    }

    private void checkResetVerified(Boolean resetVerified, LocalDateTime expiryTime) {
        if (!Boolean.TRUE.equals(resetVerified)) {
            throw new RuntimeException("Avval Telegramga yuborilgan kodni tasdiqlang");
        }

        if (expiryTime == null || expiryTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kod muddati tugagan. Qaytadan kod oling");
        }
    }

    private void checkResetCode(String savedCode, LocalDateTime expiryTime, String inputCode) {
        if (savedCode == null || expiryTime == null) {
            throw new RuntimeException("Reset kodi mavjud emas");
        }

        if (expiryTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kod muddati tugagan");
        }

        if (!savedCode.equals(inputCode)) {
            throw new BadCredentialsException("Kod noto‘g‘ri");
        }
    }

    @Transactional
    public void resendCode(Long telegramId) {
        Student student = repository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("Student topilmadi"));

        LocalDateTime now = LocalDateTime.now();

        if (student.getBlockedUntil() != null && student.getBlockedUntil().isAfter(now)) {
            throw new RuntimeException("Juda ko‘p urinish bo‘ldi. Keyinroq qayta urinib ko‘ring");
        }

        if (student.getCodeExpiryTime() != null && student.getCodeExpiryTime().isAfter(now)) {
            throw new RuntimeException("Iltimos, avval yuborilgan koddan foydalaning. 5 daqiqa kuting");
        }

        int resendCount = student.getResendCount() == null ? 0 : student.getResendCount();
        resendCount++;
        student.setResendCount(resendCount);

        if (resendCount >= 3) {
            student.setBlockedUntil(now.plusMinutes(15));
            student.setResendCount(0);
            repository.save(student);
            throw new RuntimeException("Ko‘p marta kod so‘raldi. 15 daqiqaga bloklandi");
        }

        String code = generateCode();
        student.setVerificationCode(code);
        student.setCodeExpiryTime(now.plusMinutes(5));
        repository.save(student);

        sendCode(student.getTelegramId(), code);
    }

    @Transactional
    public StudentResponseDto updateStudent(UUID id, StudentUpdateDto updateDto) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student topilmadi"));

        if (updateDto.getFullName() != null && !updateDto.getFullName().isBlank()) {
            student.setFullName(updateDto.getFullName());
        }

        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank()) {
            if (!student.getUsername().equals(updateDto.getUsername()) &&
                    repository.existsByUsername(updateDto.getUsername())) {
                throw new IllegalArgumentException("Username band");
            }
            student.setUsername(updateDto.getUsername());
        }

        if (updateDto.getPhoneNumber() != null && !updateDto.getPhoneNumber().isBlank()) {
            String normalizedPhone = normalizePhoneNumber(updateDto.getPhoneNumber());

            if (!student.getPhoneNumber().equals(normalizedPhone) &&
                    repository.existsByPhoneNumber(normalizedPhone)) {
                throw new IllegalArgumentException("Bu telefon raqam band");
            }

            student.setPhoneNumber(normalizedPhone);
        }

        repository.save(student);
        return mapper.map(student, StudentResponseDto.class);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public void linkTelegramAndSendCode(String phoneNumber, Long telegramId) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        Student student = repository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Bu raqam bilan student topilmadi"));

        student.setTelegramId(telegramId);

        if (student.getVerificationCode() == null || student.getCodeExpiryTime() == null
                || student.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            String code = generateCode();
            student.setVerificationCode(code);
            student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }

        repository.save(student);
        sendCode(telegramId, student.getVerificationCode());
    }

    public void verifyCodeInsideBot(Long telegramId, String code) {
        Student student = repository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("Avval /start bosing va raqamingizni yuboring"));

        if (student.isVerified()) {
            return;
        }

        if (student.getVerificationCode() == null || student.getCodeExpiryTime() == null) {
            throw new IllegalArgumentException("Tasdiqlash kodi mavjud emas");
        }

        if (student.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Kod muddati tugagan");
        }

        if (!student.getVerificationCode().equals(code)) {
            throw new BadCredentialsException("Kod noto‘g‘ri");
        }

        student.setVerified(true);
        student.setVerificationCode(null);
        student.setCodeExpiryTime(null);
        repository.save(student);
    }

    @Transactional
    public void verifyTelegram(TelegramVerifyDto dto) {

        Student student = repository.findByUsername(dto.getUsername()).orElse(null);

        if (student != null) {
            if (student.isVerified()) {
                return;
            }

            checkVerificationCode(
                    student.getVerificationCode(),
                    student.getCodeExpiryTime(),
                    dto.getCode()
            );

            student.setVerified(true);
            student.setVerificationCode(null);
            student.setCodeExpiryTime(null);

            repository.save(student);
            return;
        }

        Admin admin = adminRepository.findByUsername(dto.getUsername());

        if (admin != null) {
            checkVerificationCode(
                    admin.getVerificationCode(),
                    admin.getCodeExpiryTime(),
                    dto.getCode()
            );

            admin.setVerified(true);
            admin.setVerificationCode(null);
            admin.setCodeExpiryTime(null);

            adminRepository.save(admin);
            return;
        }

        Teacher teacher = teacherRepository.findByUsername(dto.getUsername());

        if (teacher != null) {
            checkVerificationCode(
                    teacher.getVerificationCode(),
                    teacher.getCodeExpiryTime(),
                    dto.getCode()
            );

            teacher.setVerified(true);
            teacher.setVerificationCode(null);
            teacher.setCodeExpiryTime(null);

            teacherRepository.save(teacher);
            return;
        }

        throw new IllegalArgumentException("User topilmadi");
    }

    private void validatePasswordAndStatus(String encodedPassword, String rawPassword, boolean verified, String message) {
        if (!verified) {
            throw new BadCredentialsException("Akkaunt hali tasdiqlanmagan");
        }

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException(message);
        }
    }

    private void checkVerificationCode(String savedCode, LocalDateTime expiryTime, String inputCode) {
        if (savedCode == null || expiryTime == null) {
            throw new RuntimeException("Kod mavjud emas");
        }

        if (expiryTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kod muddati tugagan");
        }

        if (!savedCode.equals(inputCode)) {
            throw new BadCredentialsException("Kod noto‘g‘ri");
        }
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void sendCode(@NotNull Long telegramId, String code) {
        telegramBots.sendMessageToChat(
                telegramId,
                "Sizning verification codingiz: " + code
        );
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^\\d+]", "");

        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }

        return cleaned;
    }
}