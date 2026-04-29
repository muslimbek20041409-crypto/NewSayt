package com.example.testsaytproyekt.teacher.service;

import com.example.testsaytproyekt.teacher.dto.*;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper=new ModelMapper();
    public List<TeacherResponseDto> getAll() {
        return teacherRepository.findAll().stream()
                .map(professor->modelMapper
                        .map(professor,TeacherResponseDto.class))
                .toList();
    }

    public TeacherResponseDto getbyid(UUID id) {
        return modelMapper.map(teacherRepository.findById(id)
                .get(),TeacherResponseDto.class);
    }

    public TeacherResponseDto create(TeacherCreateDto professorCreateDto) {
        return null;
    }

    public TeacherResponseDto updateId(UUID id, TeacherUpdateDto professorUpdateDto) {
        Teacher professor = teacherRepository.findById(id).get();
        modelMapper.map(professorUpdateDto,professor);
        Teacher save = teacherRepository.save(professor);
        return modelMapper.map(save,TeacherResponseDto.class);
    }

    public void delete(UUID id) {
        teacherRepository.deleteById(id);
    }
}
