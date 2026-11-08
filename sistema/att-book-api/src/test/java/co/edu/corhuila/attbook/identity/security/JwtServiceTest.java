package co.edu.corhuila.attbook.identity.security;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String ISSUER = "att-book-api";

    private JwtConfiguration configuration;
    private JwtDecoder decoder;
    private JwtService service;

    @BeforeEach
    void setUp() {
        configuration = configurationFor(
                "test-only-secret-key-with-at-least-32-bytes"
        );

        decoder = configuration.jwtDecoder();

        service = new JwtService(
                configuration.jwtEncoder(),
                Clock.systemUTC(),
                ISSUER,
                3600
        );
    }

    @Test
    void issuesSignedTokenWithUserIdentity() {
        String token = service.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );

        Jwt jwt = decoder.decode(token);

        assertAll(
                () -> assertEquals("10", jwt.getSubject()),
                () -> assertEquals(
                        "teacher@example.test",
                        jwt.getClaimAsString("email")
                ),
                () -> assertEquals(
                        "TEACHER",
                        jwt.getClaimAsString("role")
                ),
                () -> assertEquals(
                        ISSUER,
                        jwt.getClaimAsString("iss")
                ),
                () -> assertNotNull(jwt.getId()),
                () -> assertNotNull(jwt.getExpiresAt()),
                () -> assertEquals(
                        3600,
                        jwt.getExpiresAt().getEpochSecond()
                                - jwt.getIssuedAt().getEpochSecond()
                )
        );
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        JwtConfiguration otherConfiguration = configurationFor(
                "another-test-secret-key-with-at-least-32-bytes"
        );

        JwtService otherService = new JwtService(
                otherConfiguration.jwtEncoder(),
                Clock.systemUTC(),
                ISSUER,
                3600
        );

        String token = otherService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void rejectsExpiredToken() {
        Clock pastClock = Clock.fixed(
                Instant.now().minusSeconds(7200),
                ZoneOffset.UTC
        );

        JwtService pastService = new JwtService(
                configuration.jwtEncoder(),
                pastClock,
                ISSUER,
                3600
        );

        String token = pastService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void rejectsUnexpectedIssuer() {
        JwtService otherIssuerService = new JwtService(
                configuration.jwtEncoder(),
                Clock.systemUTC(),
                "another-application",
                3600
        );

        String token = otherIssuerService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void rejectsUnsupportedRole() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.issueToken(
                        10L,
                        "user@example.test",
                        "ADMIN"
                )
        );
    }

    @Test
    void rejectsShortSigningKey() {
        assertThrows(
                IllegalArgumentException.class,
                () -> configurationFor("short-key")
        );
    }

    private JwtConfiguration configurationFor(String rawSecret) {
        String encodedSecret = Base64.getEncoder().encodeToString(
                rawSecret.getBytes(StandardCharsets.UTF_8)
        );

        return new JwtConfiguration(encodedSecret, ISSUER);
    }
}