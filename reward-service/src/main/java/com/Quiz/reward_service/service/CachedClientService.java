package com.Quiz.reward_service.service;

import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CachedClientService {

    private final UserClient userClient;
    private final QuizClient quizClient;

    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Integer id) {
        return userClient.getUserById(id);
    }

    @Cacheable(value = "usersByName", key = "#username")
    public UserDto getUserByUsername(String username) {
        return userClient.getUserByUsername(username);
    }

    @Cacheable(value = "quizzes", key = "#id")
    public QuizDto getQuizById(Integer id) {
        return quizClient.getQuizById(id);
    }

    @Cacheable(value = "allQuizzes")
    public List<QuizDto> getAllQuizzes() {
        return quizClient.getAllQuizzes();
    }
}
