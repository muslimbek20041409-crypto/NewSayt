package com.example.testsaytproyekt.result.entity;

import com.example.testsaytproyekt.test.entity.Test;
import com.example.testsaytproyekt.users.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Result {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn( nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn( nullable = false)
    private Test test;

  @Min(0)
    private int score;
   @Min(0)
    private int totalPoints;
@Min(0)
    private double percentage;

    private LocalDateTime submittedAt = LocalDateTime.now();
}
