package com.Quiz.questionnaire_service.service;

import com.Quiz.questionnaire_service.dto.QuestionDto;
import com.Quiz.questionnaire_service.exception.QuestionNotFoundException;
import com.Quiz.questionnaire_service.exception.QuizNotFoundException;
import com.Quiz.questionnaire_service.mapper.QuestionMapper;
import com.Quiz.questionnaire_service.mapper.ReponseMapper;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Quiz;
import com.Quiz.questionnaire_service.model.Reponse;
import com.Quiz.questionnaire_service.repository.QuestionRepository;
import com.Quiz.questionnaire_service.repository.QuizRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


import static com.Quiz.questionnaire_service.mapper.QuestionMapper.toDto;


@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public Page<QuestionDto> findAllQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findAll(pageable)
                .map(QuestionMapper::toDto);
    }

    public QuestionDto findQuestionById(int id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(String.format("La question %s n'existe pas!", id)));
        return toDto(question);
    }

    public QuestionDto createQuestion(QuestionDto questionDto) {

        Quiz quiz = quizRepository.findById(questionDto.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz introuvable avec id : " + questionDto.getQuizId()));

        Question question = QuestionMapper.toEntity(questionDto, quiz);
        question.setQuiz(quiz); // Association manuelle nécessaire

        if (questionDto.getReponses() != null && !question.getReponses().isEmpty()) {
            List<Reponse> reponses = questionDto.getReponses().stream()
                    .map(dto -> ReponseMapper.toEntity(dto, question)) // tu relies chaque réponse à la question
                    .toList();
            question.setReponses(reponses);
        }

        Question saved = questionRepository.save(question);
        return toDto(saved);
    }

    public void updateQuestion(QuestionDto questionDto) {

        if (questionDto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant de la question est requis pour la mise à jour.");
        }

        Question question = questionRepository.findById(questionDto.getId())
                .orElseThrow(() -> new QuestionNotFoundException(String.format("La question %s n'existe pas!", questionDto.getId())));

        mergerQuestion(question, questionDto);
        questionRepository.save(question);
    }

    private void mergerQuestion(Question question, QuestionDto questionDto) {
        if (!StringUtils.isBlank(questionDto.getQuestion())) {
            question.setQuestion(questionDto.getQuestion());
        }

        if (questionDto.getQuizId() != null) {
            Quiz quiz = quizRepository.findById(questionDto.getQuizId())
                    .orElseThrow(() -> new IllegalArgumentException("Quiz introuvable avec id : " + questionDto.getQuizId()));
            question.setQuiz(quiz);
        }

        if (questionDto.getReponses() != null && !questionDto.getReponses().isEmpty()) {
            question.getReponses().clear();
            List<Reponse> reponses = questionDto.getReponses().stream()
                    .map(dto -> ReponseMapper.toEntity(dto, question))
                    .toList();
            question.getReponses().addAll(reponses);
        }
    }

    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }


}
