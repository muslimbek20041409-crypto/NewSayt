package com.example.testsaytproyekt.question.entity;

import com.example.testsaytproyekt.test.entity.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    @JsonIgnore
    private Test test;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_all_answers", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "answer_order")
    private List<String> allAnswers = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_true_answers", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "answer_order")
    private List<String> trueAnswers = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_left_items", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "left_order")
    private List<String> leftItems = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_right_items", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "right_order")
    private List<String> rightItems = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_correct_pairs", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "pair_order")
    private List<String> correctPairs = new ArrayList<>();

    @Column(length = 20)
    private String trueFalseAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionLevel questionLevel;

    @Min(1)
    @Column(nullable = false)
    private int points;

    public enum QuestionType {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        MATCHING,
        TRUE_FALSE
    }

    public enum QuestionLevel {
        EASY,
        MEDIUM,
        HARD
    }
}
