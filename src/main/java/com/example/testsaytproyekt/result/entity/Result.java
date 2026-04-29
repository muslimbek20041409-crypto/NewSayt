package com.example.testsaytproyekt.result.entity;

import com.example.testsaytproyekt.test.entity.Test;
import com.example.testsaytproyekt.users.entity.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    @JsonIgnore
    private Test test;

    @Min(0)
    private int score;

    @Min(0)
    private int totalPoints;

    @Min(0)
    private double percentage;

    private LocalDateTime submittedAt = LocalDateTime.now();
}
