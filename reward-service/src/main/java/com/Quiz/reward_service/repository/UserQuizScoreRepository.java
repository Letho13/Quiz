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
}
