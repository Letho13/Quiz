package com.Quiz.questionnaire_service.controller;



import com.Quiz.questionnaire_service.service.QuestionService;
import com.quiz.shared.dto.QuestionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<QuestionDto> questionDtoPage = questionService.findAllQuestions(page, size);
        List<QuestionDto> questionDtoList = questionDtoPage.getContent();
        return new ResponseEntity<>(questionDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable ("id") Integer id) {
        QuestionDto questionDto = questionService.findQuestionById (id);
        return ResponseEntity.ok(questionDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Integer id,
            @RequestBody @Valid QuestionDto questionDto) {
        questionDto.setId (id);
        questionService.updateQuestion (questionDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<QuestionDto> addQuestion(@RequestBody @Valid QuestionDto questionDto) {
        return ResponseEntity.ok(questionService.createQuestion(questionDto));
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable ("id") Integer id) {
        questionService.deleteQuestion (id);
    }

}
