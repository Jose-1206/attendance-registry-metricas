
package co.edu.corhuila.attbook.academic.sessions.qr;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "qr_codes")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected QrCode() {
        // Constructor requerido por JPA
    }

    public QrCode(
            Long sessionId,
            String token,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.sessionId = sessionId;
        this.token = token;
        this.createdAt = LocalDateTime.ofInstant(
                createdAt, ZoneOffset.UTC
        );
        this.expiresAt = LocalDateTime.ofInstant(
                expiresAt, ZoneOffset.UTC
        );
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getToken() {
        return token;
    }

    public Instant getCreatedAt() {
        return createdAt.toInstant(ZoneOffset.UTC);
    }

    public Instant getExpiresAt() {
        return expiresAt.toInstant(ZoneOffset.UTC);
    }

    public boolean isActive() {
        return active;
    }
}
