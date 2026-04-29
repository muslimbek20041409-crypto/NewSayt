package com.example.testsaytproyekt.telegrambot.bot;

import com.example.testsaytproyekt.admin.entity.Admin;
import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class TelegramBots extends TelegramLongPollingBot {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;


    private final String botToken="8673320482:AAE5L50A9SXLTpclbOW4HaF4xSpXetsEvWQ";

    private final String botUsername="bot2026cheker_bot";

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage()) {
                return;
            }

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (message.hasText()) {
                String text = message.getText().trim();

                if ("/start".equalsIgnoreCase(text)) {
                    askForPhoneNumber(chatId);
                    return;
                }

                if (text.matches("\\d{4,8}")) {
                    handleVerificationCode(chatId, text);
                    return;
                }

                sendMessageToChat(chatId,
                        """
                        Iltimos, /start bosing.
                        Keyin telefon raqamingizni yuboring.
                        """
                );
                return;
            }

            if (message.hasContact()) {
                handleContact(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askForPhoneNumber(Long chatId) {
        KeyboardButton button = new KeyboardButton("📱 Raqamni yuborish");
        button.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(
                """
                Assalomu alaykum.
                Tasdiqlash uchun telefon raqamingizni yuboring.
                """
        );
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegramga contact so‘rovini yuborishda xatolik", e);
        }
    }

    private void handleContact(Message message) {
        Contact contact = message.getContact();
        Long chatId = message.getChatId();
        Long telegramId = message.getFrom().getId();

        if (contact.getUserId() == null || !contact.getUserId().equals(telegramId)) {
            sendMessageToChat(chatId, "Faqat o‘zingizning raqamingizni yuboring.");
            return;
        }

        String phone = normalizePhoneNumber(contact.getPhoneNumber());

        Optional<Student> studentOpt = studentRepository.findByPhoneNumber(phone);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setTelegramId(telegramId);
            ensureVerificationCode(student);
            studentRepository.save(student);

            sendMessageToChat(chatId,
                    "Sizning verification codingiz: " + student.getVerificationCode());
            return;
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByPhoneNumber(phone);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            teacher.setTelegramId(telegramId);
            ensureVerificationCode(teacher);
            teacherRepository.save(teacher);

            sendMessageToChat(chatId,
                    "Sizning verification codingiz: " + teacher.getVerificationCode());
            return;
        }

        Optional<Admin> adminOpt = adminRepository.findByphoneNumber(phone);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setTelegramId(telegramId);
            ensureVerificationCode(admin);
            adminRepository.save(admin);

            sendMessageToChat(chatId,
                    "Sizning verification codingiz: " + admin.getVerificationCode());
            return;
        }

        sendMessageToChat(chatId, "Bu raqam bilan ro‘yxatdan o‘tilmagan.");
    }

    private void handleVerificationCode(Long chatId, String code) {
        Optional<Student> studentOpt = studentRepository.findByTelegramId(chatId);
        if (studentOpt.isPresent()) {
            verifyStudent(studentOpt.get(), code, chatId);
            return;
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByTelegramId(chatId);
        if (teacherOpt.isPresent()) {
            verifyTeacher(teacherOpt.get(), code, chatId);
            return;
        }

        Optional<Admin> adminOpt = adminRepository.findByTelegramId(chatId);
        if (adminOpt.isPresent()) {
            verifyAdmin(adminOpt.get(), code, chatId);
            return;
        }

        sendMessageToChat(chatId, "Avval /start bosing va raqamingizni yuboring.");
    }

    private void verifyStudent(Student student, String code, Long chatId) {
        if (!isCodeValid(student.getVerificationCode(), student.getCodeExpiryTime(), code)) {
            sendMessageToChat(chatId, "Kod noto‘g‘ri yoki muddati tugagan.");
            return;
        }

        student.setVerified(true);
        student.setVerificationCode(null);
        student.setCodeExpiryTime(null);
        studentRepository.save(student);

        sendMessageToChat(chatId, "Student akkaunti tasdiqlandi ✅");
    }

    private void verifyTeacher(Teacher teacher, String code, Long chatId) {
        if (!isCodeValid(teacher.getVerificationCode(), teacher.getCodeExpiryTime(), code)) {
            sendMessageToChat(chatId, "Kod noto‘g‘ri yoki muddati tugagan.");
            return;
        }

        teacher.setVerified(true);
        teacher.setVerificationCode(null);
        teacher.setCodeExpiryTime(null);
        teacherRepository.save(teacher);

        sendMessageToChat(chatId, "Teacher akkaunti tasdiqlandi ✅");
    }

    private void verifyAdmin(Admin admin, String code, Long chatId) {
        if (!isCodeValid(admin.getVerificationCode(), admin.getCodeExpiryTime(), code)) {
            sendMessageToChat(chatId, "Kod noto‘g‘ri yoki muddati tugagan.");
            return;
        }

        admin.setVerified(true);
        admin.setVerificationCode(null);
        admin.setCodeExpiryTime(null);
        adminRepository.save(admin);

        sendMessageToChat(chatId, "Admin akkaunti tasdiqlandi ✅");
    }

    private boolean isCodeValid(String savedCode, LocalDateTime expiryTime, String inputCode) {
        return savedCode != null
                && expiryTime != null
                && expiryTime.isAfter(LocalDateTime.now())
                && savedCode.equals(inputCode);
    }

    private void ensureVerificationCode(Student student) {
        if (student.getVerificationCode() == null
                || student.getCodeExpiryTime() == null
                || student.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            student.setVerificationCode(generateCode());
            student.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private void ensureVerificationCode(Teacher teacher) {
        if (teacher.getVerificationCode() == null
                || teacher.getCodeExpiryTime() == null
                || teacher.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            teacher.setVerificationCode(generateCode());
            teacher.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private void ensureVerificationCode(Admin admin) {
        if (admin.getVerificationCode() == null
                || admin.getCodeExpiryTime() == null
                || admin.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            admin.setVerificationCode(generateCode());
            admin.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        }
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private String normalizePhoneNumber(String phone) {
        String normalized = phone.replaceAll("[^\\d+]", "");
        if (!normalized.startsWith("+")) {
            normalized = "+" + normalized;
        }
        return normalized;
    }

    public void sendMessageToChat(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegramga xabar yuborishda xatolik", e);
        }
    }
}