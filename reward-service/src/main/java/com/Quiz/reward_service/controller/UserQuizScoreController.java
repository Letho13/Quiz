package com.Quiz.reward_service.controller;


import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.Quiz.reward_service.repository.UserQuizScoreRepository;
import com.Quiz.reward_service.service.UserQuizScoreService;
import com.quiz.shared.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/score")
public class UserQuizScoreController {

    private final UserQuizScoreService userQuizScoreService;
    private final UserClient userClient;
    private final QuizClient quizClient;

    @GetMapping("/ranking")
    public ResponseEntity<List<UserQuizScoreDto>> getTopTenUserQuizScore(@RequestParam("quizId") Integer quizId) {

        List<UserQuizScoreDto> topTen = userQuizScoreService.rankingTopTen(quizId);
        return ResponseEntity.ok(topTen);
    }

    @PostMapping("/new")
    public UserQuizScore newAttempt( @RequestParam("userId") Integer userId, @RequestParam("quizId") Integer quizId) {
        return userQuizScoreService.createNewAttempt(userId,quizId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserQuizScoreDto>> getScoresByUser(@PathVariable Integer userId) {
        List<UserQuizScoreDto> scores = userQuizScoreService.getScoresByUser(userId);
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/finalize")
    public ResponseEntity<UserQuizScoreDto> finalizeQuiz(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("quizId") Integer quizId,
            @RequestBody List<Status> userAnswers
    ) {
        UserQuizScore attempt = userQuizScoreService.finalizeCurrentAttempt(userId, quizId, userAnswers);

        UserQuizScoreDto dto = new UserQuizScoreDto(
                userClient.getUserById(userId).getUsername(),
                attempt.getScore(),
                quizClient.getQuizById(quizId).getTitle()
        );

        return ResponseEntity.ok(dto);
    }

}
