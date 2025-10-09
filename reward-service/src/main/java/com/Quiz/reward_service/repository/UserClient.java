package com.Quiz.reward_service.repository;

import com.Quiz.reward_service.configuration.FeignConfig;
import com.quiz.shared.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url ="${gateway.url}/USER-SERVICE" ,configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/user/{id}")
    UserDto getUserById(@PathVariable("id")  Integer id);

    //utilisé pour récupérer les username le classement de chaque quiz

    @GetMapping("/api/user/by-username/{username}")
    UserDto getUserByUsername(@PathVariable("username")  String username);

}
