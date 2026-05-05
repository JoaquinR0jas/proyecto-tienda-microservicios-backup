package DJ.TIENDA.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Activa Spring Security en este microservicio
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF porque usamos JWT, no sesiones
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones, cada request trae su token
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/validate").permitAll() // Estas rutas son públicas
                .anyRequest().authenticated()); // Todo lo demás requiere autenticación
        return http.build();
    }
}