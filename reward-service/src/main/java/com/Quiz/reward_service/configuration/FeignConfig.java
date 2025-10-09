package com.Quiz.reward_service.configuration;

import com.Quiz.reward_service.service.ServiceJwtUtil;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor

public class FeignConfig {

    private final ServiceJwtUtil serviceJwtUtil;


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = serviceJwtUtil.generateGatewayToken();
            log.info("➡️ Propagating SERVICE JWT: {}", token.substring(0, 15) + "...");
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

}



