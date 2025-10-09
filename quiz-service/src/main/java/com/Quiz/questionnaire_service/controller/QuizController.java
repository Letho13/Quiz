package com.Quiz.questionnaire_service.controller;


import com.Quiz.questionnaire_service.service.QuizService;
import com.quiz.shared.dto.QuizDto;
import com.quiz.shared.dto.QuizType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/all")
    public ResponseEntity<List<QuizDto>> getAllQuiz() {
        List<QuizDto> quizDtoList = quizService.findAllQuizNoPaging();
        return new ResponseEntity<>(quizDtoList, HttpStatus.OK);
    }

    @GetMapping("/paged")
    public ResponseEntity<List<QuizDto>> getAllQuizPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<QuizDto> quizDtoPage = quizService.findAllQuiz(page, size);
        List<QuizDto> quizDtoList = quizDtoPage.getContent();
        return new ResponseEntity<>(quizDtoList, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public List <QuizDto> getQuizByType(@PathVariable("type") QuizType type) {
        return quizService.findByType(type);
    }

    @GetMapping("/quiz/types")
    public ResponseEntity<QuizType[]> getQuizTypes() {
        return ResponseEntity.ok(QuizType.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable("id") Integer id) {
        QuizDto quizDto = quizService.findQuizById (id);
        return ResponseEntity.ok(quizDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateQuiz(
            @PathVariable("id") Integer id,
            @RequestBody @Valid QuizDto quizDto) {
        quizDto.setId (id);
        quizService.updateQuiz(quizDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<QuizDto> addQuiz(@RequestBody @Valid QuizDto quizDto) {
        return ResponseEntity.ok(quizService.createQuiz(quizDto));
    }


    @DeleteMapping("/{id}")
    public void deleteQuiz (@PathVariable ("id") Integer id) {
        quizService.deleteQuiz (id);
    }

}
