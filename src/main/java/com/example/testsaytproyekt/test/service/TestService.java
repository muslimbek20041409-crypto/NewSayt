package com.example.testsaytproyekt.test.service;

import com.example.testsaytproyekt.question.entity.Question;
import com.example.testsaytproyekt.question.dto.QuestionCreateDto;
import com.example.testsaytproyekt.teacher.entity.Teacher;
import com.example.testsaytproyekt.teacher.repository.TeacherRepository;
import com.example.testsaytproyekt.test.repository.TestRepository;
import com.example.testsaytproyekt.test.specification.TestSpecification;
import com.example.testsaytproyekt.test.dto.TestCreateDto;
import com.example.testsaytproyekt.test.dto.TestMapper;
import com.example.testsaytproyekt.test.dto.TestResponseDto;
import com.example.testsaytproyekt.test.dto.TestUpdateDto;
import com.example.testsaytproyekt.test.entity.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TestService {

    private final TestRepository testRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public Page<TestResponseDto> getTests(
            UUID teacherId,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Specification<Test> spec = Specification
                .where(TestSpecification.hasTeacherId(teacherId))
                .and(TestSpecification.titleContains(keyword));

        return testRepository.findAll(spec, pageable)
                .map(TestMapper::toDto);
    }

    public TestResponseDto create(TestCreateDto dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Test test = new Test();
        test.setTitle(dto.getTitle());
        test.setTeacher(teacher);
        test.setCreatedAt(LocalDateTime.now());

        if (dto.getQuestions() != null) {
            List<Question> questions = dto.getQuestions()
                    .stream()
                    .map(qDto -> toQuestion(qDto, test))
                    .toList();

            test.setQuestions(questions);
        }

        Test saved = testRepository.save(test);

        return TestMapper.toDto(saved);
    }

    public TestResponseDto update(UUID id, TestUpdateDto dto) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        test.setTitle(dto.getTitle());

        if (dto.getQuestions() != null) {
            test.getQuestions().clear();

            List<Question> questions = dto.getQuestions()
                    .stream()
                    .map(qDto -> toQuestion(qDto, test))
                    .toList();

            test.getQuestions().addAll(questions);
        }

        Test saved = testRepository.save(test);

        return TestMapper.toDto(saved);
    }

    public void delete(UUID id) {
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Test not found");
        }

        testRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TestResponseDto getById(UUID id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        return TestMapper.toDto(test);
    }

    private Question toQuestion(QuestionCreateDto dto, Test test) {
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

        return question;
    }
}