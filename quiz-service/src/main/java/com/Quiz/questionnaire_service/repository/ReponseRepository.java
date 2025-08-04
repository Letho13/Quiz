package com.Quiz.questionnaire_service.repository;


import com.Quiz.questionnaire_service.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReponseRepository extends JpaRepository<Reponse, Integer> {
}
