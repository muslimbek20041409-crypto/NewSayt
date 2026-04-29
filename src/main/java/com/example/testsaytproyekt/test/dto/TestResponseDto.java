package com.example.testsaytproyekt.test.dto;

import com.example.testsaytproyekt.question.dto.QuestionResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResponseDto {

    private UUID id;
    private String title;

    private UUID teacherId;
    private String teacherFullName;

    private List<QuestionResponseDto> questions;

    private LocalDateTime createdAt;
}