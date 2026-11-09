
package co.edu.corhuila.attbook.identity.security;

import java.time.Instant;
import java.util.Set;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("TEACHER", "STUDENT");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/register",
                                "/auth/login"
                        ).permitAll()
                        .requestMatchers(
                                "/cursos",
                                "/cursos/**"
                        ).hasRole("TEACHER")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/sessions"
                        ).hasRole("TEACHER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/sessions/courses/**"
                        ).hasAnyRole("TEACHER", "STUDENT")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        this::convertAuthentication
                                )
                        )
                );

        return http.build();
    }

    private AttendanceAuthentication convertAuthentication(Jwt jwt) {
        try {
            Long userId = Long.valueOf(jwt.getSubject());
            String email = jwt.getClaimAsString("email");
            String role = jwt.getClaimAsString("role");
            Instant expiration = jwt.getExpiresAt();

            if (userId <= 0
                    || email == null
                    || email.isBlank()
                    || role == null
                    || !ALLOWED_ROLES.contains(role)
                    || expiration == null) {
                throw new IllegalArgumentException(
                        "Required identity claims are missing or invalid"
                );
            }

            return new AttendanceAuthentication(
                    jwt,
                    userId,
                    email,
                    role
            );
        } catch (RuntimeException exception) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token"),
                    "Invalid token identity"
            );
        }
    }
}
