package com.Quiz.reward_service.repository;

import com.Quiz.reward_service.configuration.FeignConfig;
import com.quiz.shared.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url ="http://localhost:8222/USER-SERVICE" ,configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/api/user/{id}")
    UserDto getUserById(@PathVariable("id")  Integer id);

    @GetMapping("/api/user/by-username/{username}")
    UserDto getUserByUsername(@PathVariable("username")  String username);

}
