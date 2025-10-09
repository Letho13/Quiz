package com.Quiz.questionnaire_service.serviceTest;

import com.Quiz.questionnaire_service.exception.QuizNotFoundException;
import com.Quiz.questionnaire_service.mapper.QuizMapper;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;
import com.Quiz.questionnaire_service.repository.QuizRepository;
import com.Quiz.questionnaire_service.service.QuizService;
import com.quiz.shared.dto.QuizDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    // Nous devons moquer le Mapper car il est appelé à la fin.
    // Cependant, le Mapper est statique. Pour le tester correctement,
    // nous allons faire une vérification de la taille finale dans le cas
    // où le mappage DTO n'est pas l'objectif du test (ce qui est le cas ici).
    // Dans un vrai projet, on pourrait utiliser des bibliothèques pour mocker les statiques.
    // Ici, nous allons simplement vérifier que le Quiz passé au mapper a la bonne taille.

    @InjectMocks
    private QuizService quizService;

    private Quiz mockQuiz;
    private QuizDto mockQuizDto;
    private final Integer QUIZ_ID = 1;
    private final int MAX_QUESTIONS = 10;

    @BeforeEach
    void setUp() {
        // Initialisation de la BDD mockée (Question et Quiz)
        mockQuiz = new Quiz();
        mockQuiz.setId(QUIZ_ID);
        mockQuiz.setTitle("Quiz Test");

        // Initialisation du DTO mocké
        mockQuizDto = new QuizDto();
        mockQuizDto.setId(QUIZ_ID);
        mockQuizDto.setTitle("Quiz Test");
    }

    /**
     * Crée une liste de questions factices (Question::new n'est pas idéal, mais suffit ici
     * pour le test de la taille de la liste).
     */
    private List<Question> createMockQuestions(int count) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Question q = new Question();
            q.setId(i + 1);
            q.setQuestion("Question " + (i + 1));
            questions.add(q);
        }
        return questions;
    }

    // --- Scénario 1 : Plus de 10 questions (Sélection aléatoire active) ---
    @Test
    void findQuizById_ShouldReturnTenQuestions_WhenMoreThanTenExist() {
        // GIVEN: 20 questions disponibles
        int totalQuestions = 20;
        List<Question> questions = createMockQuestions(totalQuestions);
        mockQuiz.setQuestions(questions);

        // Configuration du Mock Repository
        when(quizRepository.findById(QUIZ_ID)).thenReturn(Optional.of(mockQuiz));

        // Note sur le mappeur statique :
        // Puisque QuizMapper est statique, nous ne pouvons pas le mocker directement.
        // L'objectif de ce test est de vérifier que la logique de sélection aléatoire
        // du service a modifié l'entité 'mockQuiz' pour ne contenir que 10 questions.
        // C'est cet état modifié que le QuizMapper recevra et mappera.

        // WHEN
        // Exécution de la méthode
        quizService.findQuizById(QUIZ_ID);

        // THEN
        // Vérification que le Repository a été appelé
        verify(quizRepository, times(1)).findById(QUIZ_ID);

        // Vérification CRITIQUE : La liste de questions dans l'entité a été réduite à 10
        // (La logique de tirage aléatoire a bien fonctionné)
        assertEquals(MAX_QUESTIONS, mockQuiz.getQuestions().size(),
                "La liste de questions devrait être réduite à " + MAX_QUESTIONS);
    }

    // --- Scénario 2 : Moins de 10 questions (Sélection aléatoire inactive) ---
    @Test
    void findQuizById_ShouldReturnAllQuestions_WhenLessThanTenExist() {
        // GIVEN: 7 questions disponibles
        int totalQuestions = 7;
        List<Question> questions = createMockQuestions(totalQuestions);
        mockQuiz.setQuestions(questions);

        // Configuration du Mock Repository
        when(quizRepository.findById(QUIZ_ID)).thenReturn(Optional.of(mockQuiz));

        // WHEN
        quizService.findQuizById(QUIZ_ID);

        // THEN
        // Vérification que le Repository a été appelé
        verify(quizRepository, times(1)).findById(QUIZ_ID);

        // Vérification CRITIQUE : La taille de la liste n'a pas été modifiée
        assertEquals(totalQuestions, mockQuiz.getQuestions().size(),
                "Toutes les questions devraient être renvoyées si le total est < 10.");
    }

    // --- Scénario 3 : Quiz non trouvé (Exception) ---
    @Test
    void findQuizById_ShouldThrowException_WhenQuizDoesNotExist() {
        // GIVEN
        when(quizRepository.findById(QUIZ_ID)).thenReturn(Optional.empty());

        // WHEN / THEN
        // Vérification que l'exception QuizNotFoundException est levée
        assertThrows(QuizNotFoundException.class, () -> {
            quizService.findQuizById(QUIZ_ID);
        }, "Une QuizNotFoundException devrait être levée si le quiz n'existe pas.");

        // Vérification que l'appel au Repository a eu lieu
        verify(quizRepository, times(1)).findById(QUIZ_ID);
    }
}
