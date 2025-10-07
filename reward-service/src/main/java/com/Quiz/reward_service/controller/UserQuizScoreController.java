package com.Quiz.reward_service.controller;


import com.Quiz.reward_service.dto.QuizRankingDto;
import com.Quiz.reward_service.dto.ReponseTempsDto;
import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.Quiz.reward_service.service.UserQuizScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/score")
public class UserQuizScoreController {

    private final UserQuizScoreService userQuizScoreService;
    private final UserClient userClient;
    private final QuizClient quizClient;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("Utilisateur non authentifié ou JWT invalide");
        }

        Long userIdLong = jwt.getClaim("userId");
        return userIdLong.intValue(); // conversion safe en Integer
    }

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
    public UserQuizScore newAttempt(@RequestParam("quizId") Integer quizId) {
        Integer userId = getCurrentUserId();
        return userQuizScoreService.createNewAttempt(userId, quizId);
    }

    @GetMapping("/user/best")
    public ResponseEntity<List<UserQuizScoreDto>> getBestScoresByUser() {
        Integer userId = getCurrentUserId();
        List<UserQuizScoreDto> scores = userQuizScoreService.getBestScoresByUser(userId);
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/finalize")
    public ResponseEntity<UserQuizScoreDto> finalizeQuiz(
            @RequestParam("quizId") Integer quizId,
            @RequestBody List<ReponseTempsDto> userAnswers
    ) {
        Integer userId = getCurrentUserId();
        UserQuizScore attempt = userQuizScoreService.finalizeCurrentAttempt(userId, quizId, userAnswers);

        UserQuizScoreDto dto = new UserQuizScoreDto(
                userClient.getUserById(userId).getUsername(),
                attempt.getScore(),
                quizClient.getQuizById(quizId).getTitle()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/last/{quizId}")
    public ResponseEntity<UserQuizScoreDto> getLastScore(
            @PathVariable Integer quizId) {

        Integer userId = getCurrentUserId();

        UserQuizScore lastAttempt = userQuizScoreService
                .findLastCompletedAttempt(userId, quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Aucune tentative finalisée trouvée pour ce quiz."));

        UserQuizScoreDto dto = new UserQuizScoreDto(
                userClient.getUserById(userId).getUsername(),
                lastAttempt.getScore(),
                quizClient.getQuizById(quizId).getTitle()
        );

        return ResponseEntity.ok(dto);
    }


}
