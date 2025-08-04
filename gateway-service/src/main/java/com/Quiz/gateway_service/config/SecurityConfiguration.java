package com.Quiz.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration de la sécurité pour l'application.
 * Configure les règles de sécurité WebFlux, l'authentification via JWT,
 * le CORS, et les utilisateurs en mémoire.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://quiz-front"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }


//    private final JwtUtil jwtUtil;
//
//
//    public SecurityConfiguration(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }

//    /**
//     * Configure la chaîne de filtres de sécurité WebFlux.
//     * Désactive CSRF et form login, autorise certaines routes, et ajoute le filtre JWT.
//     *
//     * @param http configuration HTTP réactive
//     * @param jwtAuthenticationFilter filtre d'authentification JWT
//     * @return la chaîne de filtres de sécurité
//     */
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
//
//        http
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
//                )
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                        .pathMatchers("/login", "/actuator/health/*").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION);
//
//        return http.build();
//    }

//    /**
//     * Fournit un gestionnaire d'authentification personnalisé basé sur JWT.
//     *
//     * @param userDetailsService service utilisateur en mémoire
//     * @return gestionnaire d'authentification JWT
//     */
//    @Bean
//    public ReactiveAuthenticationManager reactiveAuthenticationManager(MapReactiveUserDetailsService userDetailsService) {
//        return new JwtAuthenticationManager(jwtUtil, userDetailsService);
//    }
//
//    /**
//     * Fournit le filtre d'authentification JWT à ajouter à la chaîne de sécurité.
//     *
//     * @param userDetailsService service utilisateur en mémoire
//     * @return filtre d'authentification JWT
//     */
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(MapReactiveUserDetailsService userDetailsService) {
//        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
//    }
//
//    /**
//     * Configure le CORS pour autoriser les appels depuis le front-end.
//     *
//     * @return filtre CORS configuré
//     */
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://front-service"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }
//
//    /**
//     * Crée un utilisateur en mémoire avec le rôle "USER".
//     * Utilisé pour l'accès admin en local et docker.
//     *
//     * @return service utilisateur avec un seul utilisateur "admin"
//     */
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("admin")
//                .password("{noop}password")
//                .roles("USER")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

}
