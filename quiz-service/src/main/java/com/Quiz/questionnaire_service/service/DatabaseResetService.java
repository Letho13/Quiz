package com.Quiz.questionnaire_service.service;

import com.Quiz.questionnaire_service.repository.QuestionRepository;
import com.Quiz.questionnaire_service.repository.QuizRepository;
import com.Quiz.questionnaire_service.repository.ReponseRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseResetService {

    private final ReponseRepository reponseRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final EntityManager em;

    @Transactional
    public void resetDatabase() {
        // 1️⃣ Supprimer tout
        reponseRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();

        // 2️⃣ Reset auto-increment
        em.createNativeQuery("ALTER TABLE quiz ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE question ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE reponse ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
