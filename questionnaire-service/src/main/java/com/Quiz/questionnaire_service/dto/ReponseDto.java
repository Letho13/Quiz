package com.Quiz.questionnaire_service.dto;

import com.Quiz.questionnaire_service.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReponseDto {

    private Integer id;

    private Integer questionId;

    @NotBlank(message = "Le texte de la réponse est obligatoire.")
    private String reponse;

    @NotNull(message = "Le statut (juste/faux) est requis.")
    private Status status;

}
