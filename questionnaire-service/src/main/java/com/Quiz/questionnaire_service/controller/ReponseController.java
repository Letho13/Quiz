package com.Quiz.questionnaire_service.controller;

import com.Quiz.questionnaire_service.dto.ReponseDto;
import com.Quiz.questionnaire_service.service.ReponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reponse")
public class ReponseController {

    private final ReponseService reponseService;

    @GetMapping
    public ResponseEntity<List<ReponseDto>> findAll(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ) {
        Page<ReponseDto> reponseDtoPage = reponseService.findAllReponse(page, size);
        List<ReponseDto> reponseDtoList = reponseDtoPage.getContent();
        return new ResponseEntity<>(reponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReponseDto> findById(@PathVariable Integer id) {
        ReponseDto reponseDto = reponseService.findReponseById(id);
        return ResponseEntity.ok(reponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReponse(
            @PathVariable Integer id,
            @RequestBody @Valid ReponseDto reponseDto) {
        reponseDto.setId(id);
        reponseService.updateReponse(reponseDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<ReponseDto> addReponse(@RequestBody @Valid ReponseDto reponseDto) {
        return ResponseEntity.ok(reponseService.createReponse(reponseDto));
    }

    @DeleteMapping("/{id}")
    public void deleteReponse(@PathVariable ("id") Integer id) {
        reponseService.deleteReponse (id);
    }


}
