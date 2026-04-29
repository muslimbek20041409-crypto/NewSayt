package com.example.testsaytproyekt.question.dto;

import com.example.testsaytproyekt.question.entity.Question.QuestionLevel;
import com.example.testsaytproyekt.question.entity.Question.QuestionType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDto {

    private UUID id;

    private String questionText;
    private QuestionType questionType;

    private List<String> allAnswers;
    private List<String> trueAnswers;
    private List<String> leftItems;
    private List<String> rightItems;
    private List<String> correctPairs;
    private String trueFalseAnswer;

    private QuestionLevel questionLevel;
    private int points;
}