package com.Quiz.reward_service.controllerTest;

import com.Quiz.reward_service.controller.UserQuizScoreController;
import com.Quiz.reward_service.dto.QuizRankingDto;
import com.Quiz.reward_service.dto.ReponseTempsDto;
import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
import com.Quiz.reward_service.repository.QuizClient;
import com.Quiz.reward_service.repository.UserClient;
import com.Quiz.reward_service.service.UserQuizScoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.shared.dto.Status;
import com.quiz.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserQuizScoreController.class)
class UserQuizScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserQuizScoreService userQuizScoreService;

    @MockBean
    private UserClient userClient;

    @MockBean
    private QuizClient quizClient;

    private static final Integer MOCKED_USER_ID = 5;
    private static final Integer MOCKED_QUIZ_ID = 10;
    private JwtAuthenticationToken mockJwtAuthenticationToken;

    @BeforeEach
    void setUp() {
        // Configuration d'un JWT mocké pour simuler l'utilisateur 5
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user_5")
                .claim("userId", MOCKED_USER_ID.longValue()) // Le contrôleur lit ce champ
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        // Création d'un token d'authentification à utiliser avec MockMvc
        mockJwtAuthenticationToken = new JwtAuthenticationToken(
                jwt,
                Collections.singletonList(new SimpleGrantedAuthority("SCOPE_user")),
                jwt.getClaimAsString("sub")
        );
    }

    // --- Utility method for authentication ---
    private RequestPostProcessor authenticated() {
        return authentication(mockJwtAuthenticationToken);
    }

    @Test
    @DisplayName("GET /ranking doit retourner le top 10 des scores pour un quizId donné")
    void getTopTenUserQuizScore_ShouldReturnOkAndTopTenList() throws Exception {
        // Given
        UserQuizScoreDto score1 = UserQuizScoreDto.builder().username("Alice").score(90).quizTitle("Quiz X").build();
        UserQuizScoreDto score2 = UserQuizScoreDto.builder().username("Bob").score(85).quizTitle("Quiz X").build();
        List<UserQuizScoreDto> topScores = Arrays.asList(score1, score2);

        when(userQuizScoreService.rankingTopTen(MOCKED_QUIZ_ID)).thenReturn(topScores);

        // When & Then
        mockMvc.perform(get("/api/score/ranking")
                        .param("quizId", MOCKED_QUIZ_ID.toString())
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Alice"))
                .andExpect(jsonPath("$[1].score").value(85));

        verify(userQuizScoreService, times(1)).rankingTopTen(MOCKED_QUIZ_ID);
    }



    @Test
    @DisplayName("GET /ranking/all doit retourner le classement de tous les quiz")
    void getAllRankings_ShouldReturnOkAndAllRankings() throws Exception {
        // Given
        QuizRankingDto rankingA = QuizRankingDto.builder().quizTitle("Quiz A").myScore(50).build();
        QuizRankingDto rankingB = QuizRankingDto.builder().quizTitle("Quiz B").myScore(null).build();
        List<QuizRankingDto> allRankings = Arrays.asList(rankingA, rankingB);

        when(userQuizScoreService.getAllQuizzesRanking()).thenReturn(allRankings);

        // When & Then
        mockMvc.perform(get("/api/score/ranking/all")
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].quizTitle").value("Quiz A"))
                .andExpect(jsonPath("$[0].myScore").value(50))
                .andExpect(jsonPath("$[1].myScore").doesNotExist()); // myScore est null

        verify(userQuizScoreService, times(1)).getAllQuizzesRanking();
    }

    @Test
    @DisplayName("POST /new doit créer une nouvelle tentative et retourner l'objet UserQuizScore")
    void newAttempt_ShouldReturnNewAttempt() throws Exception {
        // Given
        UserQuizScore newAttempt = UserQuizScore.builder()
                .id(101).userId(MOCKED_USER_ID).quizId(MOCKED_QUIZ_ID).attemptNumber(1).score(0).build();

        when(userQuizScoreService.createNewAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID)).thenReturn(newAttempt);

        // When & Then
        mockMvc.perform(post("/api/score/new")
                        .param("quizId", MOCKED_QUIZ_ID.toString())
                        .with(authenticated())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.userId").value(MOCKED_USER_ID));

        verify(userQuizScoreService, times(1)).createNewAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID);
    }


    @Test
    @DisplayName("GET /user/best doit retourner les meilleurs scores de l'utilisateur courant")
    void getBestScoresByUser_ShouldReturnOkAndBestScoresList() throws Exception {
        // Given
        UserQuizScoreDto score1 = UserQuizScoreDto.builder().username("User5").score(70).quizTitle("Q1").build();
        List<UserQuizScoreDto> bestScores = Collections.singletonList(score1);

        when(userQuizScoreService.getBestScoresByUser(MOCKED_USER_ID)).thenReturn(bestScores);

        // When & Then
        mockMvc.perform(get("/api/score/user/best")
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("User5"));

        verify(userQuizScoreService, times(1)).getBestScoresByUser(MOCKED_USER_ID);
    }


    @Test
    @DisplayName("POST /finalize doit finaliser le quiz et retourner le DTO du score final")
    void finalizeQuiz_ShouldReturnOkAndScoreDto() throws Exception {
        // Given
        List<ReponseTempsDto> answers = Arrays.asList(
                ReponseTempsDto.builder().status(Status.VRAI).timeRemaining(10).build(),
                ReponseTempsDto.builder().status(Status.FAUX).timeRemaining(5).build()
        );

        UserQuizScore completedAttempt = UserQuizScore.builder()
                .id(100).userId(MOCKED_USER_ID).quizId(MOCKED_QUIZ_ID).score(3).completedAt(LocalDateTime.now()).build();

        UserDto userDto = UserDto.builder().id(MOCKED_USER_ID).username("FinalUser").build();

        when(userQuizScoreService.finalizeCurrentAttempt(eq(MOCKED_USER_ID), eq(MOCKED_QUIZ_ID), any())).thenReturn(completedAttempt);
        when(userClient.getUserById(MOCKED_USER_ID)).thenReturn(userDto);
        when(quizClient.getQuizById(MOCKED_QUIZ_ID)).thenReturn(com.quiz.shared.dto.QuizDto.builder().title("Final Quiz").build());

        // When & Then
        mockMvc.perform(post("/api/score/finalize")
                        .param("quizId", MOCKED_QUIZ_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers))
                        .with(authenticated())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("FinalUser"))
                .andExpect(jsonPath("$.score").value(3))
                .andExpect(jsonPath("$.quizTitle").value("Final Quiz"));

        verify(userQuizScoreService, times(1)).finalizeCurrentAttempt(eq(MOCKED_USER_ID), eq(MOCKED_QUIZ_ID), any());
        verify(userClient, times(1)).getUserById(MOCKED_USER_ID);
        verify(quizClient, times(1)).getQuizById(MOCKED_QUIZ_ID);
    }


    @Test
    @DisplayName("GET /last/{quizId} doit retourner le dernier score finalisé")
    void getLastScore_ShouldReturnOkAndLastScoreDto() throws Exception {
        // Given
        UserQuizScore lastAttempt = UserQuizScore.builder()
                .id(99).userId(MOCKED_USER_ID).quizId(MOCKED_QUIZ_ID).score(60).completedAt(LocalDateTime.now()).build();

        UserDto userDto = UserDto.builder().id(MOCKED_USER_ID).username("LastUser").build();

        when(userQuizScoreService.findLastCompletedAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID)).thenReturn(Optional.of(lastAttempt));
        when(userClient.getUserById(MOCKED_USER_ID)).thenReturn(userDto);
        when(quizClient.getQuizById(MOCKED_QUIZ_ID)).thenReturn(com.quiz.shared.dto.QuizDto.builder().title("Last Quiz").build());

        // When & Then
        mockMvc.perform(get("/api/score/last/{quizId}", MOCKED_QUIZ_ID)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("LastUser"))
                .andExpect(jsonPath("$.score").value(60));

        verify(userQuizScoreService, times(1)).findLastCompletedAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID);
        verify(userClient, times(1)).getUserById(MOCKED_USER_ID);
    }

    @Test
    @DisplayName("GET /last/{quizId} doit retourner 404 si aucune tentative finalisée n'est trouvée (Correction 500 -> 404)")
    void getLastScore_ShouldThrowExceptionIfNoAttempt() throws Exception {
        // Given
        // Le service retourne Optional.empty()
        when(userQuizScoreService.findLastCompletedAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/score/last/{quizId}", MOCKED_QUIZ_ID)
                        .with(authenticated()))
                .andExpect(status().isNotFound()); //  L'attente passe de 500 à 404

        verify(userQuizScoreService, times(1)).findLastCompletedAttempt(MOCKED_USER_ID, MOCKED_QUIZ_ID);
    }
}
