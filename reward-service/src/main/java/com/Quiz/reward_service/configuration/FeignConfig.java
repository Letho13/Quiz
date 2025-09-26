package com.Quiz.reward_service.configuration;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
                String token = jwtAuth.getToken().getTokenValue();
                log.info("➡️ Propagating JWT to Feign: {}", token.substring(0, 15) + "...");
                requestTemplate.header("Authorization", "Bearer " + token);
            }  else {
            log.warn("Aucun JWT trouvé dans SecurityContext, rien n'est propagé !");
        }
        };
    }
}



