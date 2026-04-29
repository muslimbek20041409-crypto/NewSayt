package com.example.testsaytproyekt.test.dto;

import com.example.testsaytproyekt.question.dto.QuestionCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestCreateDto {

    @NotBlank
    private String title;

    @NotNull
    private UUID teacherId;

    private List<QuestionCreateDto> questions;
}