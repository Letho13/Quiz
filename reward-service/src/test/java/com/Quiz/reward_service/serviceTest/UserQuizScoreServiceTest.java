package com.Quiz.reward_service.serviceTest;

import com.Quiz.reward_service.dto.QuizRankingDto;
import com.Quiz.reward_service.dto.ReponseTempsDto;
import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.UserQuizScoreRepository;
import com.Quiz.reward_service.service.CachedClientService;
import com.Quiz.reward_service.service.UserQuizScoreService;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.Status;
import com.quiz.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQuizScoreServiceTest {

    @Mock
    private UserQuizScoreRepository userQuizScoreRepository;

    @Mock
    private CachedClientService cachedClientService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserQuizScoreService userQuizScoreService;

    private final Integer USER_ID = 1;
    private final Integer QUIZ_ID = 10;
    private UserQuizScore ongoingAttempt;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        ongoingAttempt = UserQuizScore.builder()
                .id(100)
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .score(0)
                .attemptNumber(1)
                .completedAt(null)
                .build();
    }

    @Test
    @DisplayName("Devrait retourner 5 points si la réponse est VRAI et temps >= 16s")
    void calculatePoints_ShouldReturnMaxPoints() {
        assertEquals(5, userQuizScoreService.calculatePoints(true, 16));
    }

    @Test
    @DisplayName("Devrait calculer le score, mettre à jour et sauvegarder l'attempt en cours")
    void finalizeCurrentAttempt_ShouldCalculateScoreAndSave() {
        // Given
        ReponseTempsDto reponse1 = ReponseTempsDto.builder().status(Status.VRAI).timeRemaining(10).build(); // 3 points
        ReponseTempsDto reponse3 = ReponseTempsDto.builder().status(Status.VRAI).timeRemaining(18).build(); // 5 points
        List<ReponseTempsDto> reponses = Arrays.asList(reponse1, reponse3);

        int expectedScore = 3 + 5;

        when(userQuizScoreRepository.findByUserIdAndQuizIdAndCompletedAtIsNull(USER_ID, QUIZ_ID))
                .thenReturn(Optional.of(ongoingAttempt));
        when(userQuizScoreRepository.save(any(UserQuizScore.class))).thenReturn(ongoingAttempt);

        // When
        UserQuizScore result = userQuizScoreService.finalizeCurrentAttempt(USER_ID, QUIZ_ID, reponses);

        // Then
        assertEquals(expectedScore, result.getScore());
        assertNotNull(result.getCompletedAt());
        verify(userQuizScoreRepository, times(1)).save(any(UserQuizScore.class));
    }

    @Test
    @DisplayName("Devrait créer un nouvel attempt avec le prochain numéro d'attempt (Numéro 3)")
    void createNewAttempt_ShouldSaveNewAttempt() {
        // Given
        when(userQuizScoreRepository.findMaxAttemptNumberByUserIdAndQuizId(USER_ID, QUIZ_ID))
                .thenReturn(Optional.of(2));
        when(userQuizScoreRepository.findByUserIdAndQuizIdAndCompletedAtIsNull(USER_ID, QUIZ_ID))
                .thenReturn(Optional.empty());

        when(userQuizScoreRepository.save(any(UserQuizScore.class))).thenAnswer(invocation -> {
            UserQuizScore saved = invocation.getArgument(0);
            saved.setId(101);
            return saved;
        });

        // When
        UserQuizScore result = userQuizScoreService.createNewAttempt(USER_ID, QUIZ_ID);

        // Then
        assertEquals(3, result.getAttemptNumber());
        verify(userQuizScoreRepository, times(1)).save(any(UserQuizScore.class));
    }


    @Test
    @DisplayName("Devrait retourner le Top 10 des scores mappés en DTO")
    void rankingTopTen_ShouldReturnTop10Dtos() {
        // Given
        UserQuizScore score1 = UserQuizScore.builder().id(1).userId(USER_ID).quizId(QUIZ_ID).score(50).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        UserQuizScore score2 = UserQuizScore.builder().id(2).userId(2).quizId(QUIZ_ID).score(45).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        List<UserQuizScore> topScores = Arrays.asList(score1, score2);

        QuizDto quizDto = QuizDto.builder().id(QUIZ_ID).title("Quiz Title").build();
        UserDto user1Dto = UserDto.builder().id(USER_ID).username("userA").build();
        UserDto user2Dto = UserDto.builder().id(2).username("userB").build();

        when(userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(QUIZ_ID))
                .thenReturn(topScores);

        // Mocks pour les appels clients dans la boucle (2 fois pour chaque)
        when(cachedClientService.getQuizById(QUIZ_ID)).thenReturn(quizDto);
        when(cachedClientService.getUserById(USER_ID)).thenReturn(user1Dto);
        when(cachedClientService.getUserById(2)).thenReturn(user2Dto);

        // When
        userQuizScoreService.rankingTopTen(QUIZ_ID);

        // Then
        // 2 scores => 2 appels à getUserById
        verify(cachedClientService, times(2)).getUserById(anyInt());
        // 2 scores => 2 appels à getQuizById (car dans la boucle du service)
        verify(cachedClientService, times(2)).getQuizById(anyInt());
    }

    @Test
    @DisplayName("Devrait retourner les meilleurs scores de l'utilisateur mappés en DTO")
    void getBestScoresByUser_ShouldReturnDtos() {
        // Given
        UserQuizScore bestScore1 = UserQuizScore.builder().id(1).userId(USER_ID).quizId(QUIZ_ID).score(50).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        UserQuizScore bestScore2 = UserQuizScore.builder().id(2).userId(USER_ID).quizId(11).score(60).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        List<UserQuizScore> bestScores = Arrays.asList(bestScore1, bestScore2);

        QuizDto quiz1Dto = QuizDto.builder().id(QUIZ_ID).title("Quiz 1").build();
        QuizDto quiz2Dto = QuizDto.builder().id(11).title("Quiz 2").build();
        UserDto userDto = UserDto.builder().id(USER_ID).username("userA").build();

        when(userQuizScoreRepository.findBestScoresByUser(USER_ID)).thenReturn(bestScores);
        // Mocks pour les appels clients dans la boucle (2 fois pour chaque)
        when(cachedClientService.getUserById(USER_ID)).thenReturn(userDto);
        when(cachedClientService.getQuizById(QUIZ_ID)).thenReturn(quiz1Dto);
        when(cachedClientService.getQuizById(11)).thenReturn(quiz2Dto);


        // When
        userQuizScoreService.getBestScoresByUser(USER_ID);

        // Then
        // 2 scores => 2 appels à getUserById
        verify(cachedClientService, times(2)).getUserById(anyInt());
        // 2 scores => 2 appels à getQuizById
        verify(cachedClientService, times(2)).getQuizById(anyInt());
    }

    @Test
    @DisplayName("Devrait retourner le classement de tous les quiz avec le score de l'utilisateur courant")
    void getAllQuizzesRanking_ShouldReturnFullRanking() {
        // Configuration du contexte de sécurité
        String USERNAME = "currentUser";
        Integer CURRENT_USER_ID = 5;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        UserDto currentUserDto = UserDto.builder().id(CURRENT_USER_ID).username(USERNAME).build();
        when(cachedClientService.getUserByUsername(USERNAME)).thenReturn(currentUserDto);

        // 1. Définir les données des Quiz
        QuizDto quiz1 = QuizDto.builder().id(1).title("Quiz A").build();
        QuizDto quiz2 = QuizDto.builder().id(2).title("Quiz B").build();
        List<QuizDto> allQuizzes = Arrays.asList(quiz1, quiz2);

        // MOCK ESSENTIEL
        when(cachedClientService.getAllQuizzes()).thenReturn(allQuizzes);

        // 2. Définir les Top Scores pour Quiz A
        UserQuizScore scoreA1 = UserQuizScore.builder().id(10).userId(100).quizId(1).score(80).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        UserQuizScore scoreA2 = UserQuizScore.builder().id(11).userId(CURRENT_USER_ID).quizId(1).score(75).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        List<UserQuizScore> topScoresQuizA = Arrays.asList(scoreA1, scoreA2);

        when(userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(1))
                .thenReturn(topScoresQuizA);

        when(userQuizScoreRepository
                .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(CURRENT_USER_ID, 1))
                .thenReturn(Optional.of(scoreA2)); // Mon score: 75

        // 3. Définir les Top Scores pour Quiz B
        UserQuizScore scoreB1 = UserQuizScore.builder().id(20).userId(200).quizId(2).score(90).attemptNumber(1).completedAt(LocalDateTime.now()).build();
        List<UserQuizScore> topScoresQuizB = Collections.singletonList(scoreB1);

        when(userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(2))
                .thenReturn(topScoresQuizB);

        when(userQuizScoreRepository
                .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(CURRENT_USER_ID, 2))
                .thenReturn(Optional.empty()); // Mon score: null

        // 4. Définir les utilisateurs mockés (pour les leaders dans les boucles)
        UserDto user100 = UserDto.builder().id(100).username("Leader").build();
        UserDto user200 = UserDto.builder().id(200).username("Second").build();

        // MOCK DE L'APPEL PAR ID POUR TOUS LES UTILISATEURS APPARANTS DANS LES SCORES
        when(cachedClientService.getUserById(100)).thenReturn(user100);
        when(cachedClientService.getUserById(200)).thenReturn(user200);
        when(cachedClientService.getUserById(CURRENT_USER_ID)).thenReturn(currentUserDto);

        // When
        List<QuizRankingDto> result = userQuizScoreService.getAllQuizzesRanking();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Vérification Quiz A (Noms de getters corrigés)
        QuizRankingDto rankingA = result.get(0);
        assertEquals(75, rankingA.getMyScore());
        assertEquals(2, rankingA.getRanking().size());
        assertEquals("Leader", rankingA.getRanking().get(0).getUsername());

        // Vérification Quiz B (Noms de getters corrigés)
        QuizRankingDto rankingB = result.get(1);
        assertNull(rankingB.getMyScore());
        assertEquals(1, rankingB.getRanking().size());
        assertEquals("Second", rankingB.getRanking().get(0).getUsername());

        // VÉRIFICATIONS (times(3) car 100, 200 et 5 sont appelés)
        verify(cachedClientService, times(1)).getAllQuizzes();
        verify(cachedClientService, times(1)).getUserByUsername(USERNAME);
        verify(cachedClientService, times(3)).getUserById(anyInt());
        verify(userQuizScoreRepository, times(2))
                .findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(anyInt());
        verify(userQuizScoreRepository, times(2))
                .findTopByUserIdAndQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(anyInt(), anyInt());
    }
}
