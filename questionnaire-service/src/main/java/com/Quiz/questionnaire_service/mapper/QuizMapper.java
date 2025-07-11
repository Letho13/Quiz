package com.Quiz.questionnaire_service.mapper;

import com.Quiz.questionnaire_service.dto.QuestionDto;
import com.Quiz.questionnaire_service.dto.QuizDto;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;

import java.util.List;
import java.util.stream.Collectors;

public class QuizMapper {

    public static QuizDto toDto(Quiz quiz) {
        if (quiz == null) return null;

        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setType(quiz.getType());
        dto.setTitle(quiz.getTitle());

        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            List<QuestionDto> questions = quiz.getQuestions().stream()
                    .map(QuestionMapper::toDto)
                    .collect(Collectors.toList());
            dto.setQuestions(questions);
        }

        return dto;
    }

    public static Quiz toEntity(QuizDto dto) {
        if (dto == null) return null;

        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setType(dto.getType());
        quiz.setTitle(dto.getTitle());

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            List<Question> questions = dto.getQuestions().stream()
                    .map(qDto -> QuestionMapper.toEntity(qDto, quiz))
                    .collect(Collectors.toList());
            quiz.setQuestions(questions);
        }

        return quiz;
    }

}
