package com.Quiz.questionnaire_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDto {

    private Integer id;

    @NotBlank(message = "Le texte de la question est obligatoire.")
    private String question;

    @NotNull(message = "Le quizId est requis.")
    private Integer quizId;

    private List<ReponseDto> reponses =  new ArrayList<>();
}


