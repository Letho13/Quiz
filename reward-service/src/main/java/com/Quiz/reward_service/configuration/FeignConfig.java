package com.Quiz.reward_service.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;


@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(auth -> auth.getCredentials().toString())
                    .block(); // à utiliser avec précaution

            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }


}
