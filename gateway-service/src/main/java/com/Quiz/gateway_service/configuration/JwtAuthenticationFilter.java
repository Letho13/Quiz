package com.Quiz.gateway_service.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Passer OPTIONS pour CORS
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();

        if (path.startsWith("/USER-SERVICE/api/auth") || path.startsWith("/actuator/health") || path.startsWith("/USER-SERVICE/api/user/add")) {
            return chain.filter(exchange);
        }

        // Extraire le JWT
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String jwt = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(jwt);

        if (username != null && jwtUtil.validateToken(jwt)) {

            String role = jwtUtil.getRoleFromToken(jwt);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(new SecurityContextImpl(auth))
                    ));
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }


}
