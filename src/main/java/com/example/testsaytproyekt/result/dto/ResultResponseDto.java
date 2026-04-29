package com.example.testsaytproyekt.result.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultResponseDto {

    private UUID id;

    private UUID studentId;
    private String studentName;

    private UUID testId;
    private String testTitle;

    private UUID teacherId;
    private String teacherName;

    private int score;
    private int totalPoints;
    private double percentage;

    private LocalDateTime submittedAt;
}