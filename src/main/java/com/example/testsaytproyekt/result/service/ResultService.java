package com.example.testsaytproyekt.result.service;

import com.example.testsaytproyekt.question.entity.Question;
import com.example.testsaytproyekt.result.dto.AnswerDto;
import com.example.testsaytproyekt.result.dto.ResultCreateDto;
import com.example.testsaytproyekt.result.dto.ResultResponseDto;
import com.example.testsaytproyekt.result.entity.Result;
import com.example.testsaytproyekt.result.repository.ResultRepository;
import com.example.testsaytproyekt.test.entity.Test;
import com.example.testsaytproyekt.test.repository.TestRepository;
import com.example.testsaytproyekt.users.entity.Student;
import com.example.testsaytproyekt.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository repository;
    private final StudentRepository studentRepository;
    private final TestRepository testRepository;

    @Transactional
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

        return toDto(repository.save(result));
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

        String correct = normalizeAnswerValue(question, question.getTrueAnswers().get(0));
        String given = normalizeAnswerValue(question, studentAnswer);

        return correct.equals(given);
    }

    private boolean checkMultipleChoice(Question question, String studentAnswer) {
        if (question.getTrueAnswers() == null || question.getTrueAnswers().isEmpty()) {
            return false;
        }

        Set<String> correctAnswers = question.getTrueAnswers()
                .stream()
                .filter(Objects::nonNull)
                .map(answer -> normalizeAnswerValue(question, answer))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        Set<String> givenAnswers = Arrays.stream(studentAnswer.split("[,;]"))
                .filter(Objects::nonNull)
                .map(answer -> normalizeAnswerValue(question, answer))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return correctAnswers.equals(givenAnswers);
    }

    private boolean checkTrueFalse(Question question, String studentAnswer) {
        if (question.getTrueFalseAnswer() == null) {
            return false;
        }

        String correct = normalizeTrueFalse(question.getTrueFalseAnswer());
        String given = normalizeTrueFalse(studentAnswer);

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
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        Set<String> givenPairs = Arrays.stream(studentAnswer.split("[,;]"))
                .filter(Objects::nonNull)
                .map(this::normalizePair)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return correctPairs.equals(givenPairs);
    }

    private String normalizeAnswerValue(Question question, String value) {
        if (value == null) return "";

        String normalized = normalize(value);

        // Agar frontend A/B/C yuborsa, uni allAnswers dagi matnga aylantiradi
        if (normalized.length() == 1 && normalized.charAt(0) >= 'A' && normalized.charAt(0) <= 'Z') {
            int index = normalized.charAt(0) - 'A';

            if (question.getAllAnswers() != null
                    && index >= 0
                    && index < question.getAllAnswers().size()) {
                return normalize(question.getAllAnswers().get(index));
            }
        }

        return normalized;
    }

    private String normalizeTrueFalse(String value) {
        if (value == null) return "";

        String v = value.trim()
                .replace("’", "'")
                .replace("‘", "'")
                .replace("`", "'")
                .toUpperCase();

        if (v.equals("HA")
                || v.equals("YES")
                || v.equals("TRUE")
                || v.equals("TOG'RI")
                || v.equals("TO‘G‘RI")
                || v.equals("TO‘G'RI")
                || v.equals("TOG‘RI")) {
            return "TRUE";
        }

        if (v.equals("YOQ")
                || v.equals("YO'Q")
                || v.equals("YO‘Q")
                || v.equals("NO")
                || v.equals("FALSE")
                || v.equals("NOTO'G'RI")
                || v.equals("NOTO‘G‘RI")
                || v.equals("NOTO‘G'RI")
                || v.equals("NOTOG'RI")) {
            return "FALSE";
        }

        return v;
    }

    private String normalize(String value) {
        if (value == null) return "";

        return value.trim()
                .replace("’", "'")
                .replace("‘", "'")
                .replace("`", "'")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String normalizePair(String value) {
        if (value == null) return "";

        return value.trim()
                .replace("’", "'")
                .replace("‘", "'")
                .replace("`", "'")
                .replaceAll("\\s+", "")
                .toLowerCase();
    }

    @Transactional(readOnly = true)
    public List<ResultResponseDto> getByTeacher(UUID teacherId) {
        return repository.findByTestTeacherId(teacherId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultResponseDto> getByTest(UUID testId) {
        return repository.findByTestId(testId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
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
