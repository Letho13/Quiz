package com.Quiz.questionnaire_service.repository;

import com.Quiz.questionnaire_service.model.Quiz;
import com.quiz.shared.dto.QuizType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    boolean existsByTitle(String title);

    List<Quiz> findByType(QuizType type);
}
