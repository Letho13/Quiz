package com.Quiz.reward_service.service;


import com.Quiz.reward_service.dto.QuizRankingDto;
import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.Quiz.reward_service.repository.UserQuizScoreRepository;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserQuizScoreService {

    private final UserQuizScoreRepository userQuizScoreRepository;
    private final QuizClient quizClient;
    private final UserClient userClient;


    public Integer pointReponseVrai() {
        return 5;
    }



    public UserQuizScore finalizeCurrentAttempt(Integer userId, Integer quizId, List<Status> userAnswers) {

        UserQuizScore currentAttempt = userQuizScoreRepository
                .findByUserIdAndQuizIdAndCompletedAtIsNull(userId, quizId)
                .orElseThrow(() -> new RuntimeException("Aucune tentative en cours"));

        QuizDto quizDto = quizClient.getQuizById(quizId);

        int score = 0;

        // Calcule le score
        for (Status answer : userAnswers) {

            if (answer == Status.VRAI) {
                score += pointReponseVrai();
            }
        }



        currentAttempt.setScore(score);
        currentAttempt.setCompletedAt(LocalDateTime.now());

        UserQuizScore saved = userQuizScoreRepository.save(currentAttempt);


        return saved;
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
            String quizTitle = quizClient.getQuizById(score.getQuizId()).getTitle();
            ranking.add(new UserQuizScoreDto(username, score.getScore(), quizTitle));
        }

        return ranking;
    }

    public Optional<UserQuizScore> findLastCompletedAttempt(Integer userId, Integer quizId) {
        return userQuizScoreRepository
                .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId, quizId);
    }

    private List<UserQuizScoreDto> toDtoList(List<UserQuizScore> scores) {
        List<UserQuizScoreDto> dtos = new ArrayList<>();
        for (UserQuizScore score : scores) {
            String quizTitle = quizClient.getQuizById(score.getQuizId()).getTitle();
            String username = userClient.getUserById(score.getUserId()).getUsername();
            dtos.add(new UserQuizScoreDto(username, score.getScore(), quizTitle));
        }
        return dtos;
    }

    public List<QuizRankingDto> getAllQuizzesRanking() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userClient.getUserByUsername(username).getId();

        List<QuizDto> quizzes = quizClient.getAllQuizzes();
        List<QuizRankingDto> result = new ArrayList<>();

        for (QuizDto quiz : quizzes) {
            List<UserQuizScore> topScores =
                    userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(quiz.getId());

            List<UserQuizScoreDto> ranking = toDtoList(topScores);

            Integer myScore = userQuizScoreRepository
                    .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(userId, quiz.getId())
                    .map(UserQuizScore::getScore)
                    .orElse(null);

            result.add(new QuizRankingDto(quiz.getId(), quiz.getTitle(), ranking, myScore));
        }

        return result;
    }

    public List<UserQuizScoreDto> getBestScoresByUser(Integer userId) {
        List<UserQuizScore> bestScores = userQuizScoreRepository.findBestScoresByUser(userId);
        return toDtoList(bestScores);
    }



//    public List<UserQuizScoreDto> getBestScoresByUser(Integer userId) {
//        List<UserQuizScore> bestScores = userQuizScoreRepository.findBestScoresByUser(userId);
//
//        List<UserQuizScoreDto> dtos = new ArrayList<>();
//        for (UserQuizScore best : bestScores) {
//            String quizTitle = quizClient.getQuizById(best.getQuizId()).getTitle();
//            String username = userClient.getUserById(best.getUserId()).getUsername();
//            dtos.add(new UserQuizScoreDto(username, best.getScore(), quizTitle));
//        }
//
//        return dtos;
//    }

//    public List<UserQuizScoreDto> getBestScoresByUser(Integer userId) {
//        List<UserQuizScore> scores = userQuizScoreRepository
//                .findByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId);
//
//        // Map pour stocker le meilleur score par quiz
//        Map<Integer, UserQuizScore> bestByQuiz = new HashMap<>();
//
//        for (UserQuizScore s : scores) {
//            bestByQuiz.merge(
//                    s.getQuizId(),
//                    s,
//                    (oldScore, newScore) -> newScore.getScore() > oldScore.getScore() ? newScore : oldScore
//            );
//        }
//
//        List<UserQuizScoreDto> dtos = new ArrayList<>();
//        for (UserQuizScore best : bestByQuiz.values()) {
//            String quizTitle = quizClient.getQuizById(best.getQuizId()).getTitle();
//            String username = userClient.getUserById(best.getUserId()).getUsername();
//            dtos.add(new UserQuizScoreDto(username, best.getScore(), quizTitle));
//        }
//
//        return dtos;
//    }



//    public List<QuizRankingDto> getAllQuizzesRanking() {
//        // Récupérer userId du JWT
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Integer userId = userClient.getUserByUsername(username).getId(); // ⚡ expose un endpoint "getUserByUsername"
//
//        List<QuizDto> quizzes = quizClient.getAllQuizzes();
//        List<QuizRankingDto> result = new ArrayList<>();
//
//        for (QuizDto quiz : quizzes) {
//            List<UserQuizScore> topScores =
//                    userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(quiz.getId());
//
//            List<UserQuizScoreDto> ranking = new ArrayList<>();
//            for (UserQuizScore score : topScores) {
//                String uname = userClient.getUserById(score.getUserId()).getUsername();
//                ranking.add(new UserQuizScoreDto(uname, score.getScore(), quiz.getTitle()));
//            }
//
//            // chercher le meilleur score du user sur ce quiz
//            Integer myScore = userQuizScoreRepository
//                    .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(userId, quiz.getId())
//                    .map(UserQuizScore::getScore)
//                    .orElse(null);
//
//            result.add(new QuizRankingDto(quiz.getId(), quiz.getTitle(), ranking, myScore));
//        }
//
//        return result;
//    }

}




