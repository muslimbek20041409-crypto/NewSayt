package com.example.testsaytproyekt.question.service;

import com.example.testsaytproyekt.question.dto.QuestionCreateDto;
import com.example.testsaytproyekt.question.dto.QuestionResponseDto;
import com.example.testsaytproyekt.question.entity.Question;
import com.example.testsaytproyekt.question.repository.QuestionRepository;
import com.example.testsaytproyekt.test.entity.Test;
import com.example.testsaytproyekt.test.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    // ➕ ADD QUESTION
    public QuestionResponseDto add(UUID testId, QuestionCreateDto dto) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        Question question = new Question();

        question.setTest(test);
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setAllAnswers(dto.getAllAnswers());
        question.setTrueAnswers(dto.getTrueAnswers());
        question.setLeftItems(dto.getLeftItems());
        question.setRightItems(dto.getRightItems());
        question.setCorrectPairs(dto.getCorrectPairs());
        question.setTrueFalseAnswer(dto.getTrueFalseAnswer());
        question.setQuestionLevel(dto.getQuestionLevel());
        question.setPoints(dto.getPoints());

        Question saved = questionRepository.save(question);

        return toDto(saved);
    }

    // ✏️ UPDATE QUESTION
    public QuestionResponseDto update(UUID questionId, QuestionCreateDto dto) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setAllAnswers(dto.getAllAnswers());
        question.setTrueAnswers(dto.getTrueAnswers());
        question.setLeftItems(dto.getLeftItems());
        question.setRightItems(dto.getRightItems());
        question.setCorrectPairs(dto.getCorrectPairs());
        question.setTrueFalseAnswer(dto.getTrueFalseAnswer());
        question.setQuestionLevel(dto.getQuestionLevel());
        question.setPoints(dto.getPoints());

        Question updated = questionRepository.save(question);

        return toDto(updated);
    }

    // ❌ DELETE QUESTION
    public void delete(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

    // 📥 GET QUESTIONS BY TEST
    public List<QuestionResponseDto> getByTest(UUID testId) {

        return questionRepository.findByTestId(testId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 🔁 MAPPER
    private QuestionResponseDto toDto(Question q) {
        return QuestionResponseDto.builder()
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
                .build();
    }
}