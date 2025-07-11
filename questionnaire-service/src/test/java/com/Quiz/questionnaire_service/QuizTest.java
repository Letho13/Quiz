package com.Quiz.questionnaire_service;

import com.Quiz.questionnaire_service.dto.QuizDto;
import com.Quiz.questionnaire_service.exception.QuizNotFoundException;
import com.Quiz.questionnaire_service.mapper.QuizMapper;
import com.Quiz.questionnaire_service.model.Quiz;
import com.Quiz.questionnaire_service.model.QuizType;
import com.Quiz.questionnaire_service.repository.QuizRepository;
import com.Quiz.questionnaire_service.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuizTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createQuizShouldReturnSavedQuiz() {

        QuizDto quizDto = new QuizDto();
        quizDto.setType(QuizType.ACTUALITE);

        Quiz quiz = QuizMapper.toEntity(quizDto);
        Quiz savedQuiz = new Quiz();
        savedQuiz.setId(1);
        savedQuiz.setType(QuizType.ACTUALITE);

        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        QuizDto result = quizService.createQuiz(quizDto);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(QuizType.ACTUALITE, result.getType());
        verify(quizRepository, times(1)).save(any(Quiz.class));

    }

    @Test
    void findQuizById_shouldThrowException_whenNotFound() {
        when(quizRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.findQuizById(999));
    }

    @Test
    void updateQuiz_shouldThrowException_whenIdIsNull() {
        QuizDto quizDto = new QuizDto(); // no ID
        assertThrows(IllegalArgumentException.class, () -> quizService.updateQuiz(quizDto));
    }

    @Test
    void deleteQuiz_shouldCallRepository() {
        // Act
        quizService.deleteQuiz(3);

        // Assert
        verify(quizRepository, times(1)).deleteById(3);
    }

}
