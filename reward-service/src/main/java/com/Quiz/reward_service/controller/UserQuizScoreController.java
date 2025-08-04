package com.Quiz.reward_service.controller;


import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.UserClient;
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

    @GetMapping("/ranking")
    public ResponseEntity<List<UserQuizScoreDto>> getTopTenUserQuizScore(@RequestParam Integer quizId) {

        List<UserQuizScoreDto> topTen = userQuizScoreService.rankingTopTen(quizId);
        return ResponseEntity.ok(topTen);
    }

    @GetMapping("/live")
    public ResponseEntity<UserQuizScoreDto> getUserQuizScore(@RequestParam Integer userId, @RequestParam Integer quizId, @RequestParam Status status, @RequestParam int nbQuestionsRepondues) {
        UserQuizScore score = userQuizScoreService.calculatePointLive(userId, quizId, status, nbQuestionsRepondues);
        UserQuizScoreDto dto = new UserQuizScoreDto();
        dto.setScore(score.getScore());
        dto.setUsername(userClient.getUserById(userId).getUsername());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/retry")
    public UserQuizScore newAttempt( @RequestParam Integer userId, @RequestParam Integer quizId) {
        return userQuizScoreService.createNewAttempt(userId,quizId);
    }

}
