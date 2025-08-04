package com.quiz.shared.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private Integer id;

    private Integer quizId;

    private String question;

    private List<ReponseDto> reponses;
}