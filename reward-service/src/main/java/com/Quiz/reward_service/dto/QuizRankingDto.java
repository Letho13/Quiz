package com.Quiz.reward_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizRankingDto {
    private Integer quizId;
    private String quizTitle;
    private List<UserQuizScoreDto> ranking;
    private Integer myScore;
}
