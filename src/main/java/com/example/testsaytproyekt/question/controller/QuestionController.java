package com.example.testsaytproyekt.question.controller;

import com.example.testsaytproyekt.question.service.QuestionService;
import com.example.testsaytproyekt.question.dto.QuestionCreateDto;
import com.example.testsaytproyekt.question.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/test/{testId}")
    public ResponseEntity<QuestionResponseDto> add(
            @PathVariable UUID testId,
            @RequestBody QuestionCreateDto dto
    ) {
        return ResponseEntity.ok(questionService.add(testId, dto));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponseDto> update(
            @PathVariable UUID questionId,
            @RequestBody QuestionCreateDto dto
    ) {
        return ResponseEntity.ok(questionService.update(questionId, dto));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID questionId) {
        questionService.delete(questionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<QuestionResponseDto>> getByTest(
            @PathVariable UUID testId
    ) {
        return ResponseEntity.ok(questionService.getByTest(testId));
    }
}