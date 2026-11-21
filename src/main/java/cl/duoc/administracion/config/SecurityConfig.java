package cl.duoc.administracion.config;

import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Seguridad del microservicio de administracion (estructura del arquetipo).
 * - ADMIN: gestion completa de categorias, publicaciones y comentarios.
 * - MODERADOR: consulta y moderacion (banear / reactivar).
 */
@Configuration
public class SecurityConfig {

    private static final String[] RUTAS_ADMINISTRACION = {
        "/api/categorias/**",
        "/api/publicaciones/**",
        "/api/comentarios/**"
    };

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Health check usado por el despliegue en EC2
                .requestMatchers("/actuator/health/**").permitAll()

                // Moderacion: banear y reactivar publicaciones y comentarios
                .requestMatchers(HttpMethod.PATCH,
                    "/api/publicaciones/*/banear",
                    "/api/publicaciones/*/reactivar",
                    "/api/comentarios/*/banear",
                    "/api/comentarios/*/reactivar")
                .hasAnyRole("ADMIN", "MODERADOR")

                // Consultas para el panel de moderacion
                .requestMatchers(HttpMethod.GET, RUTAS_ADMINISTRACION)
                .hasAnyRole("ADMIN", "MODERADOR")

                // Crear, actualizar y eliminar: solo ADMIN
                .requestMatchers(RUTAS_ADMINISTRACION)
                .hasRole("ADMIN")

                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // Validador de JWT firmado con HS256 (misma clave que usuarios-service)
    @Bean
    public JwtDecoder jwtDecoder() {

        byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);

        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe contener al menos 32 bytes");
        }

        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Convierte el claim "role" en la autoridad ROLE_<rol>
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(allowedOrigins);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }
}
