package com.Quiz.reward_service.repository;

import com.Quiz.reward_service.configuration.FeignConfig;
import com.quiz.shared.dto.QuizDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="quiz-service",url = "${gateway.url}/QUIZ-SERVICE" ,configuration = FeignConfig.class)
public interface QuizClient {

    @GetMapping("/api/quiz/{id}")
    QuizDto getQuizById(@PathVariable("id") Integer id);

    @GetMapping("/api/quiz/all")
    List<QuizDto> getAllQuizzes();
}
