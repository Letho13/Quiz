package com.Quiz.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {


    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

//        if ("admin".equals(username) && "password".equals(password)) {
//            String token = jwtUtil.generateToken(username);
//            return Mono.just(ResponseEntity.ok(Map.of("token", token)));
//        } else {
//            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials")));
//        }
        return Mono.just(ResponseEntity.ok().body(Map.of("username", username)));
    }
}
