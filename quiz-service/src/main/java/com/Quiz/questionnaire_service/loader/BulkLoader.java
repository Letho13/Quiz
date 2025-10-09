package com.Quiz.questionnaire_service.loader;

import com.Quiz.questionnaire_service.dto.QuizBulkRequest;
import com.Quiz.questionnaire_service.service.DatabaseResetService;
import com.Quiz.questionnaire_service.service.QuizBulkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class BulkLoader implements CommandLineRunner {

    private final QuizBulkService quizBulkService;
    private final DatabaseResetService databaseResetService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        //  Reset de la base (supprime tout et reset les IDs)
        databaseResetService.resetDatabase();

        // Récupération du fichier JSON depuis resources
        InputStream is = getClass().getResourceAsStream("/bulk.json");
        if (is == null) {
            System.out.println("Fichier bulk.json introuvable dans resources !");
            return;
        }

        // Lecture et transformation du JSON en objet Java
        QuizBulkRequest request = objectMapper.readValue(is, QuizBulkRequest.class);

        // Sauvegarde dans la base (avec suppression préalable)
        quizBulkService.saveBulk(request);

        System.out.println("Bulk JSON chargé avec succès !");
    }
}