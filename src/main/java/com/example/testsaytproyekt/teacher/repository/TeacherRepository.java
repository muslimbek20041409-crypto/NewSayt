package com.example.testsaytproyekt.teacher.repository;

import com.example.testsaytproyekt.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Teacher findByUsername(String username);

    Optional<Teacher> findByTelegramId(Long id);

    Optional<Teacher> findByPhoneNumber(String normalizedPhone);
}
