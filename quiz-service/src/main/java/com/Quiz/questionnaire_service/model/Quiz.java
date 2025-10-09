package com.Quiz.questionnaire_service.model;

import com.quiz.shared.dto.QuizType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import static jakarta.persistence.EnumType.*;

@Entity
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Enumerated(value = STRING)
    private QuizType type;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;

}
