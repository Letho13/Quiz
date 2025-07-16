package com.Quiz.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserDto {

    private Integer id;

    @NotBlank(message = "Le username est obligatoire.")
    private String username;

    @NotBlank(message = "L'email est obligatoire.")
    @Email
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Le role est obligatoire.")
    private String role;

}
