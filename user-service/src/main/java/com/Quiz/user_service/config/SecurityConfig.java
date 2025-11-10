package com.Quiz.user_service.config;

import com.Quiz.user_service.mapper.UserMapper;
import com.Quiz.user_service.model.User;
import com.Quiz.user_service.repository.UserRepository;
import com.quiz.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // La règle SpEL qui combine TOUT : admin, service, ou l'utilisateur lui-même
        String generalUserAccessRule = "hasRole('ADMIN') or hasRole('GATEWAY_CALL') or #username == authentication.name";
        String generalUserIdAccessRule = "hasRole('ADMIN') or hasRole('GATEWAY_CALL') or #id.toString() == authentication.principal.claims['userId'].toString()";

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .requestMatchers("/api/user/search").hasRole("ADMIN") // Recherche réservée aux admins

                        // Appliquer la nouvelle règle unifiée et complète
                        .requestMatchers("/api/user/by-username/{username}")
                        .access(new WebExpressionAuthorizationManager(generalUserAccessRule))
                        .requestMatchers("/api/user/{id}")
                        .access(new WebExpressionAuthorizationManager(generalUserIdAccessRule))

                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }


    //Admin creation at start
    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                UserDto adminDto = new UserDto();
                adminDto.setUsername("admin");
                adminDto.setPassword("Admin123!");
                adminDto.setEmail("admin@localhost");
                adminDto.setRole("ADMIN");

                User admin = UserMapper.toEntity(adminDto);
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));

                userRepository.save(admin);
                System.out.println("Admin created: admin / Admin123!");
            }
        };
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1. Extraire le "role" (pour les utilisateurs normaux)
            String role = jwt.getClaimAsString("role");
            Stream<String> roleStream = role != null ? Stream.of("ROLE_" + role) : Stream.empty();

            // 2. Extraire les "authorities" (pour les jetons de service)
            List<String> authoritiesList = jwt.getClaimAsStringList("authorities");
            Stream<String> authoritiesStream = authoritiesList != null
                    ? authoritiesList.stream().map(a -> "ROLE_" + a) // préfixage
                    : Stream.empty();

            // 3. Combiner les deux listes en une seule liste de GrantedAuthority
            return Stream.concat(roleStream, authoritiesStream)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        // Crée la clé à partir du secret du YAML
        SecretKeySpec keySpec = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(keySpec).build();
    }

}
