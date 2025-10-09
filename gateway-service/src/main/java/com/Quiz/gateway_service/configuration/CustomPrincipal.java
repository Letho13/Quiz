package com.Quiz.gateway_service.configuration;

import lombok.Data;

@Data

public class CustomPrincipal {
    private final Integer userId;
    private final String username;
    private final String role;

}