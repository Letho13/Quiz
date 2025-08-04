package com.Quiz.reward_service.service;


import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.Quiz.reward_service.repository.UserQuizScoreRepository;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQuizScoreService {

    private final UserQuizScoreRepository userQuizScoreRepository;
    private final QuizClient quizClient;
    private final UserClient userClient;


    public Integer pointReponseVrai() {
        return 5;
    }

    public UserQuizScore calculatePointLive(Integer userId, Integer quizId, Status status, int nbQuestionsRepondues) {

        QuizDto quizDto = quizClient.getQuizById(quizId);
        int totalQuestions = quizDto.getQuestions().size();

        if (nbQuestionsRepondues < totalQuestions) {
            UserQuizScore currentAttempt = userQuizScoreRepository
                    .findByUserIdAndQuizIdAndCompletedAtIsNull(userId, quizId)
                    .orElseGet(() -> {
                        int nextAttemptNumber = userQuizScoreRepository
                                .findMaxAttemptNumberByUserIdAndQuizId(userId, quizId)
                                .orElse(0) + 1;
                        UserQuizScore newAttempt = new UserQuizScore();
                        newAttempt.setUserId(userId);
                        newAttempt.setQuizId(quizId);
                        newAttempt.setScore(0);
                        newAttempt.setAttemptNumber(nextAttemptNumber);
                        newAttempt.setCompletedAt(null);
                        return userQuizScoreRepository.save(newAttempt);
                    });

            if (status == Status.VRAI) {
                currentAttempt.setScore(currentAttempt.getScore() + pointReponseVrai());
            }

            return userQuizScoreRepository.save(currentAttempt);
        }
        return finalizeCurrentAttempt(userId, quizId);
    }

    public UserQuizScore finalizeCurrentAttempt(Integer userId, Integer quizId) {


        UserQuizScore currentAttempt = userQuizScoreRepository
                .findByUserIdAndQuizIdAndCompletedAtIsNull(userId, quizId)
                .orElseThrow(() -> new RuntimeException("Aucune tentative en cours"));

        currentAttempt.setCompletedAt(LocalDateTime.now());
       return userQuizScoreRepository.save(currentAttempt);


    }

    public UserQuizScore createNewAttempt(Integer userId, Integer quizId) {
        int nextAttemptNumber = userQuizScoreRepository
                .findMaxAttemptNumberByUserIdAndQuizId(userId, quizId)
                .orElse(0) + 1;
        if (userQuizScoreRepository.findByUserIdAndQuizIdAndCompletedAtIsNull(userId, quizId).isPresent()) {
            throw new IllegalStateException("Une tentative est déjà en cours.");
        }
        UserQuizScore newAttempt = new UserQuizScore();
        newAttempt.setUserId(userId);
        newAttempt.setQuizId(quizId);
        newAttempt.setScore(0);
        newAttempt.setAttemptNumber(nextAttemptNumber);
        newAttempt.setCompletedAt(null);

        return userQuizScoreRepository.save(newAttempt);
    }

    public List<UserQuizScoreDto> rankingTopTen (Integer quizId) {

        List<UserQuizScore> topScores = userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(quizId);

        List<UserQuizScoreDto> ranking = new ArrayList<>();

        for(UserQuizScore score : topScores) {
            String username = userClient.getUserById(score.getUserId()).getUsername();
            ranking.add(new UserQuizScoreDto(username, score.getScore()));
        }

        return ranking;
    }

}




