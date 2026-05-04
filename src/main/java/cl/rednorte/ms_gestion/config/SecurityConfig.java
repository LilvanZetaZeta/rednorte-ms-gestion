package cl.rednorte.ms_gestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${SUPABASE_JWKS_URI}")
    private String jwkSetUri;

    @Value("${SUPABASE_ANON_KEY}") 
    private String anonKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                // AQUÍ ESTÁ LA SOLUCIÓN AL 401 DEL REGISTRO
                .requestMatchers(HttpMethod.POST, "/api/usuarios/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
            
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Tu código original: Inyectamos el anonKey para que Supabase nos entregue las llaves públicas
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("apikey", anonKey);
            return execution.execute(request, body);
        });

        // Al usar jwkSetUri, Spring Boot automáticamente usa RS256
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate)
                .build();
    }
}