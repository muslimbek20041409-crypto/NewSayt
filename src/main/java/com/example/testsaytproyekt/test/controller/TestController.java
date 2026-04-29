package com.example.testsaytproyekt.test.controller;

import com.example.testsaytproyekt.test.dto.TestCreateDto;
import com.example.testsaytproyekt.test.dto.TestResponseDto;
import com.example.testsaytproyekt.test.dto.TestUpdateDto;
import com.example.testsaytproyekt.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping
    public Page<TestResponseDto> getTests(
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return testService.getTests(teacherId, keyword, page, size);
    }

    @PostMapping
    public ResponseEntity<TestResponseDto> create(@RequestBody TestCreateDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(testService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResponseDto> update(
            @PathVariable UUID id,
            @RequestBody TestUpdateDto dto
    ) {
        return ResponseEntity.ok(testService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        testService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(testService.getById(id));
    }
}