package com.Quiz.questionnaire_service.service;

import com.Quiz.questionnaire_service.dto.QuizBulkRequest;
import com.Quiz.questionnaire_service.exception.BulkSaveException;
import com.Quiz.questionnaire_service.mapper.QuizMapper;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;
import com.Quiz.questionnaire_service.model.Reponse;
import com.Quiz.questionnaire_service.repository.QuestionRepository;
import com.Quiz.questionnaire_service.repository.QuizRepository;
import com.Quiz.questionnaire_service.repository.ReponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizBulkService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ReponseRepository reponseRepository;

    /**
     * Sauvegarde tout le bulk (quizzes + questions + réponses) via cascade JPA.
     */
    @Transactional
    public void saveBulk(QuizBulkRequest request) {
        try {
            for (var quizDto : request.getQuizzes()) {
                Quiz quiz = QuizMapper.toEntity(quizDto); // convertit DTO -> Entity avec questions + réponses
                quizRepository.save(quiz); // cascade sauve tout
            }
        } catch (Exception e) {
            throw new BulkSaveException("Erreur lors de l'enregistrement du bulk : " + e.getMessage(), e);
        }
    }
}
