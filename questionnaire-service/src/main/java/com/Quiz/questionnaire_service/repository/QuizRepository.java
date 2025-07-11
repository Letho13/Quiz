package com.Quiz.questionnaire_service.repository;

import com.Quiz.questionnaire_service.model.Quiz;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    boolean existsByTitle(String title);
}
