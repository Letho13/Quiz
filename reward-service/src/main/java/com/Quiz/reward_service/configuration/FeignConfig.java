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

/**
 * Configuration globale pour les clients HTTP Feign.
 * <p>
 * Cette classe assure la sécurité des communications <b>inter-services</b>.
 * Elle configure un intercepteur qui s'exécute automatiquement avant chaque requête sortante
 * émise par ce microservice vers d'autres services (ex: vers User-Service ou Quiz-Service).
 * </p>
 */

public class FeignConfig {

    private final ServiceJwtUtil serviceJwtUtil;


    /**
     * Intercepteur de requêtes ajoutant automatiquement le jeton d'authentification.
     * <p>
     * Rôle :
     * <ol>
     * <li>Génère ou récupère un JWT valide via {@link ServiceJwtUtil}.</li>
     * <li>Injecte ce token dans le header HTTP <code>Authorization</code> (Bearer scheme).</li>
     * </ol>
     * Cela permet aux services destinataires de vérifier que la requête vient bien
     * d'une source de confiance (ce service Reward) et non d'un appel anonyme.
     * </p>
     *
     * @return L'intercepteur configuré pour Feign.
     */


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Génération du token technique pour l'appel
            String token = serviceJwtUtil.generateGatewayToken();
            log.info("➡️ Propagating SERVICE JWT: {}", token.substring(0, 15) + "...");

            // Injection du header standard d'autorisation
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

}



