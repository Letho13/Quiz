package com.Quiz.questionnaire_service.service;


import com.Quiz.questionnaire_service.exception.QuestionNotFoundException;
import com.Quiz.questionnaire_service.exception.ReponseNotFoundException;
import com.Quiz.questionnaire_service.mapper.ReponseMapper;
import com.Quiz.questionnaire_service.model.Question;
import com.Quiz.questionnaire_service.model.Reponse;
import com.Quiz.questionnaire_service.repository.QuestionRepository;
import com.Quiz.questionnaire_service.repository.ReponseRepository;
import com.quiz.shared.dto.ReponseDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.Quiz.questionnaire_service.mapper.ReponseMapper.toDto;

@Service
@RequiredArgsConstructor
public class ReponseService {

    private final ReponseRepository reponseRepository;
    private final QuestionRepository questionRepository;

    public Page<ReponseDto> findAllReponse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reponseRepository.findAll(pageable)
                .map(ReponseMapper::toDto);
    }
    public ReponseDto findReponseById(int id) {
        Reponse reponse = reponseRepository.findById(id)
                .orElseThrow(() -> new ReponseNotFoundException(String.format("La reponse %s n'existe pas!", id)));
        return toDto(reponse);
    }

    public ReponseDto createReponse(ReponseDto reponseDto) {

        Question question = questionRepository.findById(reponseDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(
                        "Question introuvable avec l'id : " + reponseDto.getQuestionId()
                ));

        Reponse reponse = ReponseMapper.toEntity(reponseDto, question);
        Reponse savedReponse = reponseRepository.save(reponse);
        return ReponseMapper.toDto(savedReponse);
    }

    public void updateReponse(ReponseDto reponseDto) {

        if (reponseDto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant de la reponse est requis pour la mise à jour.");
        }

        Reponse reponse = reponseRepository.findById(reponseDto.getId())
                .orElseThrow(() -> new ReponseNotFoundException(String.format("La reponse %s n'existe pas!", reponseDto.getId())));

        mergerReponse(reponse, reponseDto);
        reponseRepository.save(reponse);
    }

    private void mergerReponse(Reponse reponse, ReponseDto reponseDto) {
        if (!StringUtils.isBlank(reponseDto.getReponse())) {
            reponse.setReponse(reponseDto.getReponse());
        }
        if (reponseDto.getStatus() != null) {
            reponse.setStatus(reponseDto.getStatus());
        }

        if (reponseDto.getQuestionId() != null) {
            Question question = questionRepository.findById(reponseDto.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question introuvable avec id : " + reponseDto.getQuestionId()));
            reponse.setQuestion(question);
        }
    }

    public void deleteReponse(Integer id) {
        reponseRepository.deleteById(id);
    }

}
