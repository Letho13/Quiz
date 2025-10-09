package com.Quiz.reward_service.repository;

import com.Quiz.reward_service.model.UserQuizScore;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizScoreRepository extends JpaRepository<UserQuizScore,Integer> {

    Optional<UserQuizScore> findByUserIdAndQuizIdAndCompletedAtIsNull(Integer userId, Integer quizId);

    @Query("SELECT MAX(u.attemptNumber) FROM UserQuizScore u WHERE u.userId = :userId AND u.quizId = :quizId")
    Optional<Integer> findMaxAttemptNumberByUserIdAndQuizId(@Param("userId") Integer userId, @Param("quizId") Integer quizId);

    List<UserQuizScore> findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(Integer quizId);

    List<UserQuizScore> findByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Integer userId);

    Optional<UserQuizScore> findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Integer userId, Integer quizId);

    Optional<UserQuizScore> findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(Integer userId, Integer quizId);

    @Query("SELECT u FROM UserQuizScore u " +
            "WHERE u.userId = :userId AND u.completedAt IS NOT NULL " +
            "AND u.score = (" +
            "   SELECT MAX(s.score) FROM UserQuizScore s " +
            "   WHERE s.userId = :userId AND s.quizId = u.quizId AND s.completedAt IS NOT NULL" +
            ")")
    List<UserQuizScore> findBestScoresByUser(@Param("userId") Integer userId);
}
