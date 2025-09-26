package com.Quiz.questionnaire_service.service;


import com.Quiz.questionnaire_service.exception.QuizNotFoundException;
import com.Quiz.questionnaire_service.mapper.QuestionMapper;
import com.Quiz.questionnaire_service.mapper.QuizMapper;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;

import com.Quiz.questionnaire_service.repository.QuizRepository;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.QuizType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public Page<QuizDto> findAllQuiz(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quizRepository.findAll(pageable)
                .map(QuizMapper::toDto);
    }

    public QuizDto findQuizById(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(String.format("Le quiz %s n'existe pas!", id)));
        return  QuizMapper.toDto(quiz);
    }

    public List<QuizDto> findByType(QuizType type) {
       List<Quiz> quizzes = quizRepository.findByType(type);

        if (quizzes.isEmpty()) {
            throw new QuizNotFoundException(String.format("Pas de quiz associé au type : %s", type));
        }

        return quizzes.stream()
                .map(QuizMapper::toDto)
                .collect(Collectors.toList());
    }


    public QuizDto createQuiz(QuizDto quizDto) {

        boolean exists = quizRepository.existsByTitle(quizDto.getTitle());
        if (exists) {
            throw new IllegalArgumentException("Un quiz avec ce titre existe déjà : " + quizDto.getTitle());
        }

        Quiz quiz = QuizMapper.toEntity(quizDto);
        Quiz saved = quizRepository.save(quiz);
        return QuizMapper.toDto(saved);
    }


    public void updateQuiz(QuizDto quizDto) {

        if (quizDto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du quiz est requis pour la mise à jour.");
        }
        Quiz quiz = quizRepository.findById(quizDto.getId())
                .orElseThrow(() -> new QuizNotFoundException(String.format("Le quiz %s n'existe pas!", quizDto.getId())));

        mergerQuiz(quiz, quizDto);
        quizRepository.save(quiz);
    }

    private void mergerQuiz(Quiz quiz, QuizDto quizDto) {
        if (quizDto.getType() != null) {
            quiz.setType(quizDto.getType());
        }

        if (quizDto.getTitle() != null) {
            quiz.setTitle(quizDto.getTitle());
        }

        if (quizDto.getQuestions() != null && !quizDto.getQuestions().isEmpty()) {
            quiz.getQuestions().clear();
            List<Question> questions = quizDto.getQuestions().stream()
                    .map(qDto -> QuestionMapper.toEntity(qDto, quiz))
                    .toList();
            quiz.getQuestions().addAll(questions);
        }
    }


    public void deleteQuiz (Integer id) {
        quizRepository.deleteById(id);
    }


    public List<QuizDto> findAllQuizNoPaging() {
        return quizRepository.findAll()
                .stream()
                .map(QuizMapper::toDto)
                .collect(Collectors.toList());
    }
}
