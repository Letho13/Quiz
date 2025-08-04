package com.Quiz.questionnaire_service.model;

import com.quiz.shared.dto.Status;
import jakarta.persistence.*;
import lombok.Data;

import static jakarta.persistence.EnumType.*;

@Entity
@Data
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String reponse;

    @Enumerated(value = STRING)
    private Status status;
}
