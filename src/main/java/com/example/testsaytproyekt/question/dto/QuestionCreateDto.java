package com.example.testsaytproyekt.question.dto;

import com.example.testsaytproyekt.question.entity.Question.QuestionLevel;
import com.example.testsaytproyekt.question.entity.Question.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionCreateDto {

    @NotBlank
    private String questionText;

    @NotNull
    private QuestionType questionType;

    private List<String> allAnswers;
    private List<String> trueAnswers;
    private List<String> leftItems;
    private List<String> rightItems;
    private List<String> correctPairs;

    private String trueFalseAnswer;

    @NotNull
    private QuestionLevel questionLevel;

    @Min(1)
    private int points;
}