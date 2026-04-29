package com.example.testsaytproyekt.security;

import com.example.testsaytproyekt.admin.entity.Admin;
import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Student student = studentRepository.findStudentByUsername(username);
        if (student != null) {
            return student;
        }

        Teacher teacher = teacherRepository.findByUsername(username);
        if (teacher != null) {
            return teacher;
        }

        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            return admin;
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}