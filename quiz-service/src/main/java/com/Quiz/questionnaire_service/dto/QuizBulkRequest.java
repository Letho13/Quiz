package com.Quiz.questionnaire_service.dto;

import com.quiz.shared.dto.QuestionDto;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.ReponseDto;
import lombok.Data;

import java.util.List;

@Data
public class QuizBulkRequest {

        private List<QuizDto> quizzes;
        private List<QuestionDto> questions;
        private List<ReponseDto> reponses;

}
