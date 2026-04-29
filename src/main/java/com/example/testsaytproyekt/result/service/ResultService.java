package com.example.testsaytproyekt.result.service;

import com.example.testsaytproyekt.question.entity.Question;
import com.example.testsaytproyekt.result.repository.ResultRepository;
import com.example.testsaytproyekt.result.dto.AnswerDto;
import com.example.testsaytproyekt.result.dto.ResultCreateDto;
import com.example.testsaytproyekt.result.dto.ResultResponseDto;
import com.example.testsaytproyekt.result.entity.Result;
import com.example.testsaytproyekt.test.entity.Test;
import com.example.testsaytproyekt.test.repository.TestRepository;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository repository;
    private final StudentRepository studentRepository;
    private final TestRepository testRepository;

    public ResultResponseDto checkAnswers(ResultCreateDto dto) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student topilmadi"));

        Test test = testRepository.findById(dto.getTestId())
                .orElseThrow(() -> new RuntimeException("Test topilmadi"));

        int score = 0;
        int totalPoints = 0;

        for (Question question : test.getQuestions()) {
            totalPoints += question.getPoints();

            String studentAnswer = findAnswer(dto.getAnswers(), question.getId());

            if (studentAnswer == null || studentAnswer.isBlank()) {
                continue;
            }

            if (isCorrect(question, studentAnswer)) {
                score += question.getPoints();
            }
        }

        Result result = new Result();
        result.setStudent(student);
        result.setTest(test);
        result.setScore(score);
        result.setTotalPoints(totalPoints);
        result.setPercentage(totalPoints == 0 ? 0 : score * 100.0 / totalPoints);
        result.setSubmittedAt(LocalDateTime.now());

        Result saved = repository.save(result);

        return toDto(saved);
    }

    private String findAnswer(List<AnswerDto> answers, UUID questionId) {
        if (answers == null || questionId == null) return null;

        return answers.stream()
                .filter(a -> a != null && a.getQuestionId() != null)
                .filter(a -> a.getQuestionId().equals(questionId))
                .map(AnswerDto::getAnswer)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private boolean isCorrect(Question question, String studentAnswer) {
        if (question == null || question.getQuestionType() == null) {
            return false;
        }

        return switch (question.getQuestionType()) {
            case SINGLE_CHOICE -> checkSingleChoice(question, studentAnswer);
            case MULTIPLE_CHOICE -> checkMultipleChoice(question, studentAnswer);
            case TRUE_FALSE -> checkTrueFalse(question, studentAnswer);
            case MATCHING -> checkMatching(question, studentAnswer);
        };
    }

    private boolean checkSingleChoice(Question question, String studentAnswer) {
        if (question.getTrueAnswers() == null || question.getTrueAnswers().isEmpty()) {
            return false;
        }

        String correct = normalize(question.getTrueAnswers().get(0));
        String given = normalize(studentAnswer);

        return correct.equals(given);
    }

    private boolean checkMultipleChoice(Question question, String studentAnswer) {
        if (question.getTrueAnswers() == null || question.getTrueAnswers().isEmpty()) {
            return false;
        }

        Set<String> correctAnswers = question.getTrueAnswers()
                .stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        Set<String> givenAnswers = splitToSet(studentAnswer);

        return correctAnswers.equals(givenAnswers);
    }

    private boolean checkTrueFalse(Question question, String studentAnswer) {
        if (question.getTrueFalseAnswer() == null) {
            return false;
        }

        String correct = normalize(question.getTrueFalseAnswer());
        String given = normalize(studentAnswer);

        return correct.equals(given);
    }

    private boolean checkMatching(Question question, String studentAnswer) {
        if (question.getCorrectPairs() == null || question.getCorrectPairs().isEmpty()) {
            return false;
        }

        Set<String> correctPairs = question.getCorrectPairs()
                .stream()
                .filter(Objects::nonNull)
                .map(this::normalizePair)
                .collect(Collectors.toSet());

        Set<String> givenPairs = Arrays.stream(studentAnswer.split("[,;]"))
                .map(this::normalizePair)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return correctPairs.equals(givenPairs);
    }

    private Set<String> splitToSet(String value) {
        return Arrays.stream(value.split("[,;]"))
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    private String normalize(String value) {
        if (value == null) return "";
        return value.trim()
                .replace("’", "'")
                .replace("‘", "'")
                .toUpperCase();
    }

    private String normalizePair(String value) {
        if (value == null) return "";
        return value.trim()
                .replace(" ", "")
                .replace("’", "'")
                .replace("‘", "'")
                .toLowerCase();
    }

    public List<ResultResponseDto> getByTeacher(UUID teacherId) {
        return repository.findByTestTeacherId(teacherId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ResultResponseDto> getByTest(UUID testId) {
        return repository.findByTestId(testId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ResultResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ResultResponseDto> getMyResults() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repository.findByStudentId(student.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private ResultResponseDto toDto(Result result) {
        ResultResponseDto dto = new ResultResponseDto();

        dto.setId(result.getId());
        dto.setScore(result.getScore());
        dto.setTotalPoints(result.getTotalPoints());
        dto.setPercentage(result.getPercentage());
        dto.setSubmittedAt(result.getSubmittedAt());

        if (result.getStudent() != null) {
            dto.setStudentId(result.getStudent().getId());
            dto.setStudentName(result.getStudent().getFullName());
            dto.setStudentName(result.getStudent().getFullName());
        }

        if (result.getTest() != null) {
            dto.setTestId(result.getTest().getId());
            dto.setTestTitle(result.getTest().getTitle());

            if (result.getTest().getTeacher() != null) {
                dto.setTeacherId(result.getTest().getTeacher().getId());
                dto.setTeacherName(result.getTest().getTeacher().getFullName());
            }
        }

        return dto;
    }
}