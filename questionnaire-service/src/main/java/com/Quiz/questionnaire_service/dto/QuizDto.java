package com.Quiz.questionnaire_service.dto;

import com.Quiz.questionnaire_service.model.QuizType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizDto {

    private Integer id;

    @NotNull(message = "Le Type de quiz est requis.")
    private QuizType type;

    @NotNull(message = "Le Titre du quiz est requis.")
    private String title;

    private List<QuestionDto> questions = new ArrayList<>();


}
