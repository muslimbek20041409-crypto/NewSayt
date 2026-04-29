package com.example.testsaytproyekt.config;

import com.example.testsaytproyekt.admin.entity.Admin;
import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.enums.Role;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // ===== ADMIN =====
            if (adminRepository.count() == 0) {

                Admin admin = Admin.builder()
                        .fullName("Super")
                        .username("admin")
                        .password(passwordEncoder.encode("12345"))
                        .telegramId(111111111L)
                        .phoneNumber("+998954770106")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .verified(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                adminRepository.save(admin);
                System.out.println("✅ Default ADMIN created");
            }

            // ===== TEACHER =====
            if (teacherRepository.count() == 0) {

                Teacher teacher = Teacher.builder()
                        .fullName("Default")
                        .password(passwordEncoder.encode("12345"))
                        .username("teacher")
                        .telegramId(222222222L)
                        .phoneNumber("+998944770106")
                        .verified(true)
                        .enabled(true)
                        .role(Role.TEACHER)
                        .createdAt(LocalDateTime.now())
                        .build();

                teacherRepository.save(teacher);
                System.out.println("✅ Default TEACHER created");
            }

        };
    }
}