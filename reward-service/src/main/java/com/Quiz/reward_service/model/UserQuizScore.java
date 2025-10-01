package com.Quiz.reward_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_quiz_scores",
        indexes = {
                @Index(name = "idx_user_quiz", columnList = "userId, quizId"),
                @Index(name = "idx_quiz_completed_score", columnList = "quizId, completedAt, score"),
                @Index(name = "idx_user_completed", columnList = "userId, completedAt")
        })
public class UserQuizScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer quizId;
    private Integer score;
    private Integer attemptNumber;

    private LocalDateTime completedAt;
}
