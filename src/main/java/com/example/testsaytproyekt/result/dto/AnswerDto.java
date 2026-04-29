package com.example.testsaytproyekt.result.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDto {

    private UUID questionId;

    private String answer;
}