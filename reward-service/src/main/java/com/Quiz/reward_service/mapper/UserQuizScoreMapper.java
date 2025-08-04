package com.Quiz.reward_service.mapper;

import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.UserClient;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class UserQuizScoreMapper {

    private final UserClient userClient;

    private UserQuizScoreDto toDto(UserQuizScore score) {
        UserQuizScoreDto dto = new UserQuizScoreDto();
        dto.setScore(score.getScore());

        dto.setUsername(userClient.getUserById(score.getUserId()).getUsername());
        return dto;
    }

}
