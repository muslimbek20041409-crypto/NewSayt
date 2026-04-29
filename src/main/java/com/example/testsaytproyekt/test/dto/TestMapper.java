package com.example.testsaytproyekt.test.dto;

import com.example.testsaytproyekt.question.dto.QuestionResponseDto;
import com.example.testsaytproyekt.test.entity.Test;

import java.util.List;

public class TestMapper {

    public static TestResponseDto toDto(Test test) {

        List<QuestionResponseDto> questions = test.getQuestions().stream()
                .map(q -> QuestionResponseDto.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .allAnswers(q.getAllAnswers())
                        .trueAnswers(q.getTrueAnswers())
                        .leftItems(q.getLeftItems())
                        .rightItems(q.getRightItems())
                        .correctPairs(q.getCorrectPairs())
                        .trueFalseAnswer(q.getTrueFalseAnswer())
                        .questionLevel(q.getQuestionLevel())
                        .points(q.getPoints())
                        .build()
                ).toList();

        return TestResponseDto.builder()
                .id(test.getId())
                .title(test.getTitle())
                .teacherId(test.getTeacher().getId())
                .teacherFullName(test.getTeacher().getFullName())
                .questions(questions)
                .createdAt(test.getCreatedAt())
                .build();
    }
}