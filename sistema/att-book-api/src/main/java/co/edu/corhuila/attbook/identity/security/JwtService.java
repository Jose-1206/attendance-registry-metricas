package co.edu.corhuila.attbook.identity.security;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("TEACHER", "STUDENT");

    private final JwtEncoder encoder;
    private final Clock clock;
    private final String issuer;
    private final long ttlSeconds;

    public JwtService(
            JwtEncoder encoder,
            Clock clock,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.ttl-seconds}") long ttlSeconds
    ) {
        if (ttlSeconds < 60 || ttlSeconds > 86400) {
            throw new IllegalArgumentException(
                    "JWT lifetime must be between 60 and 86400 seconds"
            );
        }

        this.encoder = encoder;
        this.clock = clock;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String issueToken(Long userId, String email, String role) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("A valid user ID is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("An email is required");
        }

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Unsupported user role");
        }

        Instant now = clock.instant();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .id(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("role", role)
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}