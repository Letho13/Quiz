package com.Quiz.reward_service.dto;

import com.quiz.shared.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReponseTempsDto {
    private Status status;
    private int timeRemaining;
}
