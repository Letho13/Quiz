package com.Quiz.reward_service.mapper;

import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class UserQuizScoreMapper {

    private final UserClient userClient;
    private final QuizClient quizClient;

    private UserQuizScoreDto toDto(UserQuizScore score) {
        UserQuizScoreDto dto = new UserQuizScoreDto();
        dto.setScore(score.getScore());
        dto.setUsername(userClient.getUserById(score.getUserId()).getUsername());
        dto.setQuizTitle(quizClient.getQuizById(score.getQuizId()).getTitle());
        return dto;
    }

}
