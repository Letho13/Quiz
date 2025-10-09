package com.Quiz.questionnaire_service.controller;

import com.Quiz.questionnaire_service.dto.QuizBulkRequest;
import com.Quiz.questionnaire_service.exception.BulkSaveException;
import com.Quiz.questionnaire_service.service.QuestionService;
import com.Quiz.questionnaire_service.service.QuizBulkService;
import com.Quiz.questionnaire_service.service.QuizService;
import com.Quiz.questionnaire_service.service.ReponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizBulkController {

    private final QuizBulkService quizBulkService;

    @PostMapping("/bulk")
    public ResponseEntity<?> addBulk(@RequestBody @Valid QuizBulkRequest request) {
        try {
            quizBulkService.saveBulk(request);
            return ResponseEntity.ok("Bulk enregistré avec succès !");
        } catch (BulkSaveException e) {
            return ResponseEntity
                    .status(500)
                    .body("Échec de l'enregistrement du bulk : " + e.getMessage());
        }
    }

}
