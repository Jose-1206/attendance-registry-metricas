package co.edu.corhuila.attbook.identity.security;

import java.time.Instant;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

import org.junit.jupiter.api.Test;

import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceAuthenticationTest {

    @Test
    void exposesAuthenticatedIdentityToTheCourseModule() {
        Instant now = Instant.now();

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject("10")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("email", "teacher@example.test")
                .claim("role", "TEACHER")
                .build();

        AttendanceAuthentication authentication =
                new AttendanceAuthentication(
                        jwt,
                        10L,
                        "teacher@example.test",
                        "TEACHER"
                );

        CoursePrincipal coursePrincipal = authentication;

        assertAll(
                () -> assertTrue(authentication.isAuthenticated()),
                () -> assertEquals(10L, coursePrincipal.getUserId()),
                () -> assertEquals("TEACHER", coursePrincipal.getRole()),
                () -> assertEquals(
                        "teacher@example.test",
                        coursePrincipal.getName()
                ),
                () -> assertTrue(
                        authentication.getAuthorities().stream()
                                .anyMatch(authority ->
                                        "ROLE_TEACHER".equals(
                                                authority.getAuthority()
                                        )
                                )
                )
        );
    }
}