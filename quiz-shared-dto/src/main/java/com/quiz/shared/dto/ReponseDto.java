package com.quiz.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReponseDto {

    private Integer id;

    private Integer questionId;

    private String reponse;

    private Status status;

}
