//package com.Quiz.reward_service.configuration;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * Intercepte les requêtes HTTP pour extraire, valider
// * un token JWT et établir le contexte de sécurité si l'utilisateur est authentifié.
// */
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        // Autoriser les endpoints publics (login, health, etc.)
//        if (path.contains("/USER-SERVICE/api/auth/login") || path.contains("/actuator/health")|| path.startsWith("/USER-SERVICE/api/user/add")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Récupérer le header Authorization
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            return;
//        }
//
//        String jwt = authHeader.substring(7);
//        String username = jwtUtil.getUsernameFromToken(jwt);
//
//        if (username != null && jwtUtil.validateToken(jwt)) {
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(
//                            username,
//                            jwt,
//                            List.of()
//                    );
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // On injecte l’utilisateur dans le SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        } else {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//
//
//    }
//
//    /**
//     * Intercepte chaque requête HTTP, extrait le JWT depuis l’en-tête Authorization,
//     * le valide, puis injecte l’authentification dans le contexte de sécurité réactif si possible.
//     *
//     * @param exchange l'échange HTTP WebFlux (requête + réponse)
//     * @param chain la chaîne des filtres à exécuter
//     * @return un flux réactif continuant la chaîne de traitement
//     */
//
////
////    @Override
////    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
////        // Passer immédiatement les OPTIONS (préflight CORS)
////        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
////            return chain.filter(exchange);
////        }
////
////        String path = exchange.getRequest().getPath().value();
////
////        // Ignorer les endpoints publics
////        if (path.contains("/api/auth/login") || path.startsWith("/actuator/health")) {
////            return chain.filter(exchange);
////        }
////
////        // Extraire le JWT depuis le header Authorization
////        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
////
////        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
////            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////            return exchange.getResponse().setComplete();
////        }
////
////        String jwt = authHeader.substring(7);
////        String username = jwtUtil.getUsernameFromToken(jwt);
////
////        // Vérifier le JWT
////        if (username != null && jwtUtil.validateToken(jwt)) {
////            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
////                    username, null, List.of() // tu peux ajouter des rôles ici si tu les extrais du JWT
////            );
////
////            return chain.filter(exchange)
////                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
////                            Mono.just(new SecurityContextImpl(auth))
////                    ));
////        } else {
////            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////            return exchange.getResponse().setComplete();
////        }
////    }
//
//
//}
