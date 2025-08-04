package com.Quiz.questionnaire_service.mapper;


import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;
import com.Quiz.questionnaire_service.model.Reponse;
import com.quiz.shared.dto.QuestionDto;
import com.quiz.shared.dto.ReponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {

    public static QuestionDto toDto(Question question) {
        if (question == null) return null;

        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setQuizId(question.getQuiz() != null ? question.getQuiz().getId() : null);

        if (question.getReponses() != null) {
            List<ReponseDto> reponses = question.getReponses().stream()
                    .map(ReponseMapper::toDto)
                    .collect(Collectors.toList());
            dto.setReponses(reponses);
        }
        return dto;
    }


    // DTO -> Entity
    public static Question toEntity(QuestionDto dto, Quiz quiz) {
        if (dto == null) return null;

        Question question = new Question();
        question.setId(dto.getId());
        question.setQuestion(dto.getQuestion());
        question.setQuiz(quiz);

        if (dto.getReponses() != null) {
            List<Reponse> reponses = dto.getReponses().stream()
                    .map(rdto -> ReponseMapper.toEntity(rdto, question))
                    .collect(Collectors.toList());
            question.setReponses(reponses);
        }

        return question;
    }


}
