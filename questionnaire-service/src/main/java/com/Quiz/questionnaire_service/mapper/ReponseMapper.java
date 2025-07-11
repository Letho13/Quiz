package com.Quiz.questionnaire_service.mapper;

import com.Quiz.questionnaire_service.dto.ReponseDto;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Reponse;

public class ReponseMapper {

    // Entity -> DTO

    public static ReponseDto toDto(Reponse reponse) {
        if (reponse == null) return null;

        ReponseDto dto = new ReponseDto();
        dto.setId(reponse.getId());
        dto.setStatus(reponse.getStatus());
        dto.setReponse(reponse.getReponse());

        if(reponse.getQuestion() != null && reponse.getQuestion().getId() != null) {
            dto.setQuestionId(reponse.getQuestion().getId());
        }
        return dto;
    }

    // DTO -> Entity

    public static Reponse toEntity(ReponseDto dto, Question question) {
        if (dto == null) return null;

        Reponse reponse = new Reponse();
        reponse.setId(dto.getId());
        reponse.setReponse(dto.getReponse());
        reponse.setStatus(dto.getStatus());
        reponse.setQuestion(question);
        return reponse;
    }

}
