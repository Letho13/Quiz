package com.Quiz.reward_service.service;

import com.Quiz.reward_service.dto.QuizRankingDto;
import com.Quiz.reward_service.dto.ReponseTempsDto;
import com.Quiz.reward_service.dto.UserQuizScoreDto;
import com.Quiz.reward_service.model.UserQuizScore;
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
    private final CachedClientService cachedClientService;

    /**
     * Calcule les points attribués pour une question spécifique selon le temps restant.
     * <p>
     * Logique de calcul :
     * <ul>
     * <li>Mauvaise réponse : 0 point</li>
     * <li>16s et + : 5 points (Max)</li>
     * <li>Dégressif jusqu'à 1 point si temps > 0</li>
     * </ul>
     * </p>
     * @param isCorrect Vrai si la réponse de l'utilisateur est correcte.
     * @param timeRemainingSeconds Le temps restant au chronomètre en secondes.
     * @return Le nombre de points gagnés (0 à 5).
     */
    public int calculatePoints(boolean isCorrect, int timeRemainingSeconds) {
        if (!isCorrect) return 0;

        if (timeRemainingSeconds >= 16) {
            return 5;
        } else if (timeRemainingSeconds >= 12) {
            return 4;
        } else if (timeRemainingSeconds >= 8) {
            return 3;
        } else if (timeRemainingSeconds >= 4) {
            return 2;
        } else if (timeRemainingSeconds > 0){
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Clôture une tentative de quiz en cours, calcule le score total et enregistre la date de fin.
     * <p>
     * Cette méthode récupère la tentative "ouverte" (completedAt est null), itère sur toutes
     * les réponses fournies pour accumuler les points via {@link #calculatePoints}, puis verrouille la tentative.
     * </p>
     *
     * @param userId L'ID de l'utilisateur.
     * @param quizId L'ID du quiz concerné.
     * @param userReponseTemps Liste contenant les réponses et le temps mis pour chaque question.
     * @return L'entité UserQuizScore mise à jour et sauvegardée.
     * @throws RuntimeException Si aucune tentative active n'est trouvée.
     */
    public UserQuizScore finalizeCurrentAttempt(Integer userId, Integer quizId, List<ReponseTempsDto> userReponseTemps) {
        // Récupération de la session "active" (non terminée)
        UserQuizScore currentAttempt = userQuizScoreRepository
                .findByUserIdAndQuizIdAndCompletedAtIsNull(userId, quizId)
                .orElseThrow(() -> new RuntimeException("Aucune tentative en cours"));

        int score = 0;

        // Calcul cumulatif du score basé sur les réponses envoyées
        for (ReponseTempsDto reponse : userReponseTemps) {
            score += calculatePoints(reponse.getStatus() == Status.VRAI, reponse.getTimeRemaining());
        }

        currentAttempt.setScore(score);
        currentAttempt.setCompletedAt(LocalDateTime.now()); // Marque la tentative comme finie

        return userQuizScoreRepository.save(currentAttempt);
    }

    /**
     * Initialise une nouvelle session de quiz pour un utilisateur.
     * Vérifie qu'aucune tentative n'est déjà en cours pour éviter les doublons.
     *
     * @throws IllegalStateException Si une tentative est déjà ouverte (non terminée).
     */
    public UserQuizScore createNewAttempt(Integer userId, Integer quizId) {
        // Calcul du numéro de la prochaine tentative (incrémental)
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
        newAttempt.setCompletedAt(null); // null indique que la tentative est active

        return userQuizScoreRepository.save(newAttempt);
    }


    public List<UserQuizScoreDto> rankingTopTen (Integer quizId) {
        List<UserQuizScore> topScores = userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(quizId);
        List<UserQuizScoreDto> ranking = new ArrayList<>();

        for(UserQuizScore score : topScores) {
            // Enrichissement des données via le service de cache (nom d'utilisateur et titre du quiz)
            String username = cachedClientService.getUserById(score.getUserId()).getUsername();
            String quizTitle = cachedClientService.getQuizById(score.getQuizId()).getTitle();
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
            String quizTitle = cachedClientService.getQuizById(score.getQuizId()).getTitle();
            String username = cachedClientService.getUserById(score.getUserId()).getUsername();
            dtos.add(new UserQuizScoreDto(username, score.getScore(), quizTitle));
        }
        return dtos;
    }

    /**
     * Agrège les données de tous les quiz pour fournir un tableau de bord complet.
     * <p>
     * Pour chaque quiz existant dans le système, cette méthode construit :
     * <ol>
     * <li>Le classement des 10 meilleurs joueurs (Top 10).</li>
     * <li>Le meilleur score personnel de l'utilisateur actuellement connecté.</li>
     * </ol>
     * Cette méthode fait appel intensivement aux microservices externes via {@link CachedClientService}.
     * </p>
     *
     * @return Une liste contenant les classements globaux et le score personnel pour chaque quiz.
     */
    public List<QuizRankingDto> getAllQuizzesRanking() {
        // 1. Récupération de l'utilisateur connecté via le contexte de sécurité Spring
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = cachedClientService.getUserByUsername(username).getId();

        // 2. Récupération de la liste de tous les quiz disponibles
        List<QuizDto> quizzes = cachedClientService.getAllQuizzes();
        List<QuizRankingDto> result = new ArrayList<>();

        for (QuizDto quiz : quizzes) {
            // 3. Construction du Top 10 global pour ce quiz
            List<UserQuizScore> topScores =
                    userQuizScoreRepository.findTop10ByQuizIdAndCompletedAtIsNotNullOrderByScoreDesc(quiz.getId());

            List<UserQuizScoreDto> ranking = new ArrayList<>();
            for (UserQuizScore score : topScores) {
                String uname = cachedClientService.getUserById(score.getUserId()).getUsername();
                ranking.add(new UserQuizScoreDto(uname, score.getScore(), quiz.getTitle()));
            }

            // 4. Récupération du meilleur score personnel de l'utilisateur connecté pour ce quiz
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
}