package com.example.testsaytproyekt.users.repository;

import com.example.testsaytproyekt.users.dto.StudentSignInDto;
import com.example.testsaytproyekt.users.entity.Student;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByTelegramId(Long telegramId);
    boolean existsByTelegramId(Long telegramId);
    Student findStudentByUsername(String username);
    Optional<Student> findByPhoneNumber(String phoneNumber);
    Optional<Student> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phone);
}