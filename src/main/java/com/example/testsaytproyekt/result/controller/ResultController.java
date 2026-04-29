package com.example.testsaytproyekt.result.controller;

import com.example.testsaytproyekt.result.service.ResultService;
import com.example.testsaytproyekt.result.dto.ResultCreateDto;
import com.example.testsaytproyekt.result.dto.ResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<ResultResponseDto> check(@RequestBody ResultCreateDto dto) {
        return ResponseEntity.ok(resultService.checkAnswers(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<ResultResponseDto>> getAll() {
        return ResponseEntity.ok(resultService.getAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ResultResponseDto>> getMyResults() {
        return ResponseEntity.ok(resultService.getMyResults());
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<ResultResponseDto>> getByTest(@PathVariable UUID testId) {
        return ResponseEntity.ok(resultService.getByTest(testId));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<ResultResponseDto>> getByTeacher(@PathVariable UUID teacherId) {
        return ResponseEntity.ok(resultService.getByTeacher(teacherId));
    }
}