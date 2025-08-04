package com.quiz.shared.dto;

import lombok.Data;

@Data
public class ReponseDto {

    private Integer id;

    private Integer questionId;

    private String reponse;

    private Status status;

}
