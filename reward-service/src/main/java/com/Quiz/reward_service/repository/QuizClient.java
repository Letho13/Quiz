package com.Quiz.reward_service.repository;

import com.Quiz.reward_service.configuration.FeignConfig;
import com.quiz.shared.dto.QuizDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="quiz-service",url = "http://localhost:8080" ,configuration = FeignConfig.class)
public interface QuizClient {

    @GetMapping("/api/quiz/{id}")
    QuizDto getQuizById(@PathVariable("id") Integer id);
}
