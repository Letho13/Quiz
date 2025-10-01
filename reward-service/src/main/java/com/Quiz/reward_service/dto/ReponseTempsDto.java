package com.Quiz.reward_service.dto;

import com.quiz.shared.dto.Status;
import lombok.Data;

@Data
public class ReponseTempsDto {
    private Status status;
    private int timeRemaining;
}
