package com.Quiz.reward_service.controller;


import com.Quiz.reward_service.dto.QuizRankingDto;
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

    @GetMapping("/ranking/all")
    public ResponseEntity<List<QuizRankingDto>> getAllRankings() {
        return ResponseEntity.ok(userQuizScoreService.getAllQuizzesRanking());
    }

    @PostMapping("/new")
    public UserQuizScore newAttempt( @RequestParam("userId") Integer userId, @RequestParam("quizId") Integer quizId) {
        return userQuizScoreService.createNewAttempt(userId,quizId);
    }

    @GetMapping("/user/{userId}/best")
    public ResponseEntity<List<UserQuizScoreDto>> getBestScoresByUser(@PathVariable Integer userId) {
        List<UserQuizScoreDto> scores = userQuizScoreService.getBestScoresByUser(userId);
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

    @GetMapping("/last/{userId}/{quizId}")
    public ResponseEntity<UserQuizScoreDto> getLastScore(
            @PathVariable Integer userId,
            @PathVariable Integer quizId) {

        UserQuizScore lastAttempt = userQuizScoreService
                .findLastCompletedAttempt(userId, quizId)
                .orElseThrow(() -> new RuntimeException("Aucune tentative finalisée trouvée"));

        UserQuizScoreDto dto = new UserQuizScoreDto(
                userClient.getUserById(userId).getUsername(),
                lastAttempt.getScore(),
                quizClient.getQuizById(quizId).getTitle()
        );

        return ResponseEntity.ok(dto);
    }


}
