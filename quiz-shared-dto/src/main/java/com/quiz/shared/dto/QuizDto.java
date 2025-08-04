package com.quiz.shared.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDto {
    private Integer id;
    private String title;
    private QuizType type;
    private List<QuestionDto> questions;

}