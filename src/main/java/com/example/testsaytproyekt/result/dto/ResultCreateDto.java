package com.example.testsaytproyekt.result.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultCreateDto {

    private UUID testId;

    private List<AnswerDto> answers;
}