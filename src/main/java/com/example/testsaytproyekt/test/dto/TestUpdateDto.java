package com.example.testsaytproyekt.test.dto;

import com.example.testsaytproyekt.question.dto.QuestionCreateDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestUpdateDto {

    @NotBlank
    private String title;

    private List<QuestionCreateDto> questions;
}