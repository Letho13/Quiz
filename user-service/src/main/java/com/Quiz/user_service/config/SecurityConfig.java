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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

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
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/add").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/user/search","/api/user/by-username/{username}","/api/user/{id}").hasRole("ADMIN")
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
            String role = jwt.getClaimAsString("role");
            if (role == null) {
                return List.of();
            }
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
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
