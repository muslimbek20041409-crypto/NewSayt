package com.example.testsaytproyekt.teacher.controller;

import com.example.testsaytproyekt.teacher.service.TeacherService;
import com.example.testsaytproyekt.teacher.dto.TeacherCreateDto;
import com.example.testsaytproyekt.teacher.dto.TeacherResponseDto;
import com.example.testsaytproyekt.teacher.dto.TeacherUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

   private final TeacherService teacherService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TeacherResponseDto>>getAll(){
       return ResponseEntity.ok(teacherService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto>getById(@PathVariable UUID id){
        return ResponseEntity.ok(teacherService.getbyid(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TeacherResponseDto>create(@RequestBody TeacherCreateDto professorCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.create(professorCreateDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDto>update(@PathVariable UUID id, @RequestBody TeacherUpdateDto professorUpdateDto){
        return ResponseEntity.ok(teacherService.updateId(id,professorUpdateDto));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?>delete(@PathVariable UUID id){
        teacherService.delete(id);
        return ResponseEntity.ok().build();
    }
}
